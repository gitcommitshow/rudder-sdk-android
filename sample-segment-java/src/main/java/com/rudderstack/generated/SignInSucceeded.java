/**
 * This client was automatically generated by RudderStack Typewriter. ** Do Not Edit **
 */
package com.rudderstack.generated;

import java.util.*;
import com.rudderstack.android.sdk.core.RudderProperty;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public final class SignInSucceeded extends SerializableProperties {
    private RudderProperty properties;

    private SignInSucceeded(RudderProperty properties) {
        this.properties = properties;
    }

    protected RudderProperty toRudderProperty() {
        return properties;
    }

    /**
     * Builder for {@link SignInSucceeded}
     */
    public static class Builder {
        private RudderProperty properties;

        /**
         * Builder for {@link SignInSucceeded}
         */
        public Builder() {
            properties = new RudderProperty();
        }

        /**
         * The user's ID.
         * This property is required to generate a valid SignInSucceeded object
         */
        public Builder id(final @NonNull String id) {
            properties.put("id", id);
            
            return this;
        }

        /**
         * How many times the user has attempted to sign-in.
         * This property is optional and not required to generate a valid SignInSucceeded object
         */
        public Builder numAttempts(final @Nullable Long numAttempts) {
            properties.put("numAttempts", numAttempts);
            
            return this;
        }

        /**
         * Whether the user has indicated that the browser should store their login credentials.
         * This property is optional and not required to generate a valid SignInSucceeded object
         */
        public Builder rememberMe(final @Nullable Boolean rememberMe) {
            properties.put("rememberMe", rememberMe);
            
            return this;
        }

        /**
         * Build an instance of {@link SignInSucceeded}
         */
        public SignInSucceeded build() {
            if(properties.getProperty("id") == null){
                throw new IllegalArgumentException("SignInSucceeded missing required property: id");
            }
            return new SignInSucceeded(properties);
        }
    }
}