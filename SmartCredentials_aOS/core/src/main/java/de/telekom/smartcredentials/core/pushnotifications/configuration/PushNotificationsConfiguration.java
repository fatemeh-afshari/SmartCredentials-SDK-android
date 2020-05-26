/*
 * Copyright (c) 2020 Telekom Deutschland AG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.telekom.smartcredentials.core.pushnotifications.configuration;

import android.content.Context;

import de.telekom.smartcredentials.core.logger.ApiLoggerResolver;
import de.telekom.smartcredentials.core.pushnotifications.enums.ServiceType;
import de.telekom.smartcredentials.core.pushnotifications.enums.TpnsEnvironment;
import de.telekom.smartcredentials.core.pushnotifications.exception.InvalidConfigurationException;
import de.telekom.smartcredentials.core.pushnotifications.utils.GoogleServicesConfigurationConverter;

/**
 * Created by gabriel.blaj@endava.com at 5/14/2020
 */
public class PushNotificationsConfiguration {

    private final Context mContext;
    private final String mApiKey;
    private final String mProjectId;
    private final String mDatabaseUrl;
    private final String mApplicationId;
    private final String mGcmSenderId;
    private final String mStorageBucket;
    private final ServiceType mServiceType;
    private final String mTpnsApplicationKey;
    private final TpnsEnvironment mTpnsEnvironment;
    private final boolean mAutoSubscribeState;

    private PushNotificationsConfiguration(ConfigurationBuilder builder) {
        mContext = builder.context;
        mApiKey = builder.apiKey;
        mProjectId = builder.projectId;
        mDatabaseUrl = builder.databaseUrl;
        mApplicationId = builder.applicationId;
        mGcmSenderId = builder.gcmSenderId;
        mStorageBucket = builder.storageBucket;
        mServiceType = builder.serviceType;
        mTpnsApplicationKey = builder.tpnsApplicationKey;
        mTpnsEnvironment = builder.tpnsEnvironment;
        mAutoSubscribeState = builder.autoSubscribeState;
    }

    public Context getContext() {
        return mContext;
    }

    public String getApiKey() {
        return mApiKey;
    }

    public String getProjectId() {
        return mProjectId;
    }

    public String getDatabaseUrl() {
        return mDatabaseUrl;
    }

    public String getApplicationId() {
        return mApplicationId;
    }

    public String getGcmSenderId() {
        return mGcmSenderId;
    }

    public String getStorageBucket() {
        return mStorageBucket;
    }

    public ServiceType getServiceType() {
        return mServiceType;
    }

    public String getTpnsApplicationKey() {
        return mTpnsApplicationKey;
    }

    public TpnsEnvironment getTpnsEnvironment() {
        return mTpnsEnvironment;
    }

    public boolean getAutoSubscribeState() {
        return mAutoSubscribeState;
    }

    public static class ConfigurationBuilder {
        private Context context;
        private String apiKey;
        private String projectId;
        private String databaseUrl;
        private String applicationId;
        private String gcmSenderId;
        private String storageBucket;
        private String tpnsApplicationKey = "";
        private ServiceType serviceType;
        private TpnsEnvironment tpnsEnvironment = TpnsEnvironment.PRODUCTION;
        private boolean autoSubscribeState = true;

        /**
         * Creates a builder for a {@link PushNotificationsConfiguration} that is used for Firebase
         * initialization.
         * Builders parameters can be found in google-services.json that can be retrieved from the
         * Firebase application console
         *
         * @param context       application's {@link Context}
         * @param apiKey        {@link String} 'current_key' field from the google-services.json
         * @param projectId     {@link String} 'project_id' field from the google-services.json
         * @param databaseUrl   {@link String} 'firebase_url' field from the google-services.json
         * @param applicationId {@link String} 'mobilesdk_app_id' field from the google-services.json
         * @param gcmSenderId   {@link String} 'project_number' field from the google-services.json
         * @param storageBucket {@link String} 'storage_bucket' field from the google-services.json
         * @param serviceType   {@link ServiceType} the type of push notifications service
         */
        public ConfigurationBuilder(Context context, String apiKey, String projectId,
                                    String databaseUrl, String applicationId, String gcmSenderId,
                                    String storageBucket, ServiceType serviceType) {
            this.context = context;
            this.apiKey = apiKey;
            this.projectId = projectId;
            this.databaseUrl = databaseUrl;
            this.applicationId = applicationId;
            this.gcmSenderId = gcmSenderId;
            this.storageBucket = storageBucket;
            this.serviceType = serviceType;
        }

        /**
         * Creates a builder for a {@link PushNotificationsConfiguration} from the configuration json
         * file that must be placed in the applications assets. This configuration is used for Firebase
         * initialization.
         * The configuration json can be retrieved from the Firebase application console
         *
         * @param context     application's {@link Context}
         * @param configName  {@link String} the name of the json configuration that was stored
         *                    in the assets folder. ex. google-services.json
         * @param serviceType {@link ServiceType} the type of push notifications service
         */
        public ConfigurationBuilder(Context context, String configName, ServiceType serviceType) {
            GoogleServicesConfigurationConverter converter =
                    new GoogleServicesConfigurationConverter(context, configName);
            this.context = context;
            this.serviceType = serviceType;
            try {
                this.apiKey = converter.getApiKey();
                this.projectId = converter.getProjectId();
                this.databaseUrl = converter.getDatabaseUrl();
                this.applicationId = converter.getApplicationId();
                this.gcmSenderId = converter.getGcmSenderId();
                this.storageBucket = converter.getStorageBucket();
            } catch (InvalidConfigurationException e) {
                ApiLoggerResolver.logError(getClass().getSimpleName(), e.getLocalizedMessage());
            }
        }

        /**
         * Sets the key of the TPNS application that will be used to link it with firebase.
         *
         * @param tpnsApplicationKey {@link String} is the applicationKey that can be retrieved
         *                           from the TPNS console.
         * @return this {@link PushNotificationsConfiguration.ConfigurationBuilder} object to
         * allow for chaining of calls to set methods
         */
        public ConfigurationBuilder setTpnsApplicationKey(String tpnsApplicationKey) {
            this.tpnsApplicationKey = tpnsApplicationKey;
            return this;
        }

        /**
         * Sets the TPNS environment that determines if the registration request is made on the
         * production or the testing environment. Default value is production.
         *
         * @param tpnsEnvironment {@link TpnsEnvironment} the chosen environment.
         * @return this {@link PushNotificationsConfiguration.ConfigurationBuilder} object to
         * allow for chaining of calls to set methods
         */
        public ConfigurationBuilder setTpnsEnvironment(TpnsEnvironment tpnsEnvironment) {
            this.tpnsEnvironment = tpnsEnvironment;
            return this;
        }

        /**
         * Determines if the application subscribes for push notifications on module
         * initialization. Default value is true.
         *
         * @param autoSubscribeState {@link Boolean} the chosen auto subscribe state.
         * @return this {@link PushNotificationsConfiguration.ConfigurationBuilder} object to
         * allow for chaining of calls to set methods
         */
        public ConfigurationBuilder setAutoSubscribeState(boolean autoSubscribeState) {
            this.autoSubscribeState = autoSubscribeState;
            return this;
        }

        /**
         * Creates a {@link PushNotificationsConfiguration} with the arguments supplied to this builder.
         *
         * @return {@link PushNotificationsConfiguration} configuration object
         */
        public PushNotificationsConfiguration build() {
            return new PushNotificationsConfiguration(this);
        }
    }

}
