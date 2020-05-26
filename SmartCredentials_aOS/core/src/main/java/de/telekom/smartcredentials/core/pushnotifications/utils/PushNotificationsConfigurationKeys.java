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

package de.telekom.smartcredentials.core.pushnotifications.utils;

/**
 * Created by gabriel.blaj@endava.com at 5/26/2020
 */
public enum PushNotificationsConfigurationKeys {
    CURRENT_KEY("current_key"),
    PROJECT_ID("project_id"),
    DATABASE_URL("firebase_url"),
    APPLICATION_ID("mobilesdk_app_id"),
    GCM_SENDER_ID("project_number"),
    STORAGE_BUCKET("storage_bucket"),
    PROJECT_INFO("project_info"),
    CLIENT("client"),
    API_KEY("api_key"),
    CLIENT_INFO("client_info");

    private String mKey;

    PushNotificationsConfigurationKeys(String key) {
        mKey = key;
    }

    public String getKey() {
        return mKey;
    }
}
