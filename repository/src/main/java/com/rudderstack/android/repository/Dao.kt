package com.rudderstack.android.repository

import android.database.sqlite.SQLiteDatabase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import javax.sql.StatementEvent

class Dao<T : Entity> internal constructor(
    internal val entityClass: Class<T>,
    private val entityFactory: EntityFactory,
    internal var executorService: ExecutorService = Executors.newCachedThreadPool()
) {

    private val tableName: String = entityClass.getAnnotation(RudderEntity::class.java)?.tableName
        ?: throw IllegalArgumentException(
            "${entityClass.simpleName} is being used to generate Dao, " +
                    "but missing @RudderEntity annotation"
        )

    private var _db: SQLiteDatabase? = null
    private var todoTransactions: MutableList<Future<*>> = ArrayList(5)

    /**
     * usage
     * with(dao){
     *  entity.insert()
     * }
     *
     * @param executor
     */

    fun List<T>.insert(
        conflictResolutionStrategy: ConflictResolutionStrategy = ConflictResolutionStrategy.CONFLICT_NONE,
        insertCallback: ((rowIds: List<Long>) -> Unit)? = null
    ) {
        runTransactionOrDeferToCreation { db: SQLiteDatabase ->
            val rowIds = map {
                db.insertWithOnConflict(
                    tableName,
                    it.nullHackColumn(),
                    it.generateContentValues(),
                    conflictResolutionStrategy.type
                )
            }
            insertCallback?.invoke(rowIds)
        }

    }

    fun List<T>.delete(deleteCallback: ((rowIds: List<Int>) -> Unit)? = null) {

        runTransactionOrDeferToCreation { db ->
            val fields = entityClass.getAnnotation(RudderEntity::class.java)?.fields
            val whereClause = fields?.takeIf {
                it.isNotEmpty()
            }?.map {
                "${it.fieldName}=?s"
            }?.reduce { acc, s -> "$acc AND $s" }
            map {
                if (whereClause != null)
                    db.delete(tableName, whereClause, it.getPrimaryKeyValues())
                else -1
            }.apply {
                deleteCallback?.invoke(this)
            }


        }
    }

    fun getAll(callback: (List<T>) -> Unit) {
        runTransactionOrDeferToCreation { db: SQLiteDatabase ->
            callback.invoke(getItems(db, "SELECT * FROM $tableName"))
        }

    }

    /**
     * Get all in sync
     *
     * @return all data and null if database is not ready yet
     */
    fun getAllSync(): List<T>? {
        return _db?.let { getItems(it, "SELECT * FROM $tableName") }
    }

    fun runGetQuery(query: String, callback: (List<T>) -> Unit) {
        runTransactionOrDeferToCreation { db: SQLiteDatabase ->
            callback.invoke(getItems(db, query))
        }
    }

    /**
     * Get all in sync
     *
     * @return all data and null if database is not ready yet
     */
    fun runGetQuerySync(query: String): List<T>? {
        return getItems(_db ?: return null, query)
    }

    private fun getItems(db: SQLiteDatabase, query: String): List<T> {
        //have to use factory
        val fields = entityClass.getAnnotation(RudderEntity::class.java)?.fields
            ?: throw IllegalArgumentException("RudderEntity must have at least one field")

        val cursor = db.rawQuery(query, arrayOf())
        val items = ArrayList<T>(cursor.count)

        if (cursor.moveToFirst()) {
            do {
                val entity = fields.associate {
                    val value = when (it.type) {
                        RudderField.Type.INTEGER -> cursor.getInt(
                            cursor.getColumnIndex(it.fieldName).takeIf { it > 1 }
                                ?: throw IllegalArgumentException("No such column ${it.fieldName}")
                        )
                        RudderField.Type.TEXT -> cursor.getString(
                            cursor.getColumnIndex(it.fieldName).takeIf { it > 1 }
                                ?: throw IllegalArgumentException("No such column ${it.fieldName}"))
                    }
                    Pair(it.fieldName, value)
                }.let {
                    entityFactory.getEntity(entityClass, it)
                }
                items.add(entity)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return items
    }

    private fun runTransactionOrDeferToCreation(queryTransaction: (SQLiteDatabase) -> Unit) {
        _db?.let { db ->
            executorService.execute {
                queryTransaction.invoke(db)
            }
        } ?: run {
            executorService.submit {
                _db?.let {
                    queryTransaction.invoke(it)
                }
            }.also {
                todoTransactions.add(it)
            }
        }
    }

    internal fun setDatabase(sqLiteDatabase: SQLiteDatabase?) {
        //create fields statement
        val fields =
            entityClass.getAnnotation(RudderEntity::class.java)?.fields?.takeIf { it.isNotEmpty() }
                ?: throw IllegalArgumentException("There should be at least one field in @Entity")
        //create table if not exist
        val createTableStmt = createTableStmt(tableName, fields)
        val indexStmt = createIndexStmt(tableName, fields)
        _db = sqLiteDatabase
        //run all pending tasks
        executorService.execute {
            sqLiteDatabase?.execSQL(createTableStmt)
            indexStmt?.apply {
                sqLiteDatabase?.execSQL(indexStmt)
            }
            todoTransactions.forEach {
                it.get()
            }
        }
    }

    private fun createTableStmt(tableName: String, fields: Array<RudderField>): String? {

        var isAutoIncKeyPresent = false
        val fieldsStmt = fields.map {
            if (it.isAutoInc)
                isAutoIncKeyPresent = true
            "'${it.fieldName}' ${it.type.notation} " + //field name and type
                    // if primary and auto increment
                    if (it.primaryKey && it.isAutoInc && it.type == RudderField.Type.INTEGER) " PRIMARY KEY AUTOINCREMENT" else "" +
                            if (!it.isNullable && !it.primaryKey) " NOT NULL" else "" //specifying nullability, primary key cannot be null
        }.reduce { acc, s -> "$acc, $s" }
        val primaryKeyStmt =
            if (isAutoIncKeyPresent) "" else { //auto increment is only available for one primary key
                fields.filter { it.primaryKey }.takeIf { !it.isNullOrEmpty() }?.map {
                    it.fieldName
                }?.reduce { acc, s -> "$acc,$s" }?.let {
                    "PRIMARY KEY ($it)"
                } ?: ""
            }

        return "CREATE TABLE IF NOT EXISTS '$tableName' ($fieldsStmt ${if (primaryKeyStmt.isNotEmpty()) ", $primaryKeyStmt" else ""})"

    }

    private fun createIndexStmt(tableName: String, fields: Array<RudderField>): String? {
        val indexedFields = fields.filter {
            it.isIndex
        }.takeIf {
            it.isNotEmpty()
        } ?: return null
        val indexFieldsStmt = indexedFields.map {
//            it.indexName.takeIf { it.isNotEmpty() }?:"${it.fieldName}_idx"
            it.fieldName
        }.reduce { acc, s ->
            "$acc,$s"
        }.let {
            "($it)"
        }
        val indexName = indexedFields.map {
            "${it.fieldName}_"
        }.reduce { acc, s -> "$acc$s" }.let {
            "${it}_idx"
        }
        return "CREATE INDEX $indexName ON $tableName $indexFieldsStmt"
    }

    fun close() {
        executorService.shutdown()
        setDatabase(null)
    }

    enum class ConflictResolutionStrategy(val type: Int) {

        /**
         * When a constraint violation occurs, an immediate ROLLBACK occurs,
         * thus ending the current transaction, and the command aborts with a
         * return code of SQLITE_CONSTRAINT. If no transaction is active
         * (other than the implied transaction that is created on every command)
         * then this algorithm works the same as ABORT.
         */
        CONFLICT_ROLLBACK(SQLiteDatabase.CONFLICT_ROLLBACK),

        /**
         * When a constraint violation occurs,no ROLLBACK is executed
         * so changes from prior commands within the same transaction
         * are preserved. This is the default behavior.
         */
        CONFLICT_ABORT(SQLiteDatabase.CONFLICT_ABORT),

        /**
         * When a constraint violation occurs, the command aborts with a return
         * code SQLITE_CONSTRAINT. But any changes to the database that
         * the command made prior to encountering the constraint violation
         * are preserved and are not backed out.
         */
        CONFLICT_FAIL(SQLiteDatabase.CONFLICT_FAIL),

        /**
         * When a constraint violation occurs, the one row that contains
         * the constraint violation is not inserted or changed.
         * But the command continues executing normally. Other rows before and
         * after the row that contained the constraint violation continue to be
         * inserted or updated normally. No error is returned.
         */
        CONFLICT_IGNORE(SQLiteDatabase.CONFLICT_IGNORE),

        /**
         * When a UNIQUE constraint violation occurs, the pre-existing rows that
         * are causing the constraint violation are removed prior to inserting
         * or updating the current row. Thus the insert or update always occurs.
         * The command continues executing normally. No error is returned.
         * If a NOT NULL constraint violation occurs, the NULL value is replaced
         * by the default value for that column. If the column has no default
         * value, then the ABORT algorithm is used. If a CHECK constraint
         * violation occurs then the IGNORE algorithm is used. When this conflict
         * resolution strategy deletes rows in order to satisfy a constraint,
         * it does not invoke delete triggers on those rows.
         * This behavior might change in a future release.
         */
        CONFLICT_REPLACE(SQLiteDatabase.CONFLICT_REPLACE),

        /**
         * Use the following when no conflict action is specified.
         */
        CONFLICT_NONE(SQLiteDatabase.CONFLICT_NONE),
    }
}