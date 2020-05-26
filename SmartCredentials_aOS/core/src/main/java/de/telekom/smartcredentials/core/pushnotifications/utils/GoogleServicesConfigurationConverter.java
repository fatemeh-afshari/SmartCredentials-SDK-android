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

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import de.telekom.smartcredentials.core.logger.ApiLoggerResolver;
import de.telekom.smartcredentials.core.pushnotifications.exception.InvalidConfigurationException;

/**
 * Created by gabriel.blaj@endava.com at 5/26/2020
 */
public class GoogleServicesConfigurationConverter {

    private final static String EMPTY_CONFIGURATION_EXCEPTION = "Empty Configuration.";
    private final static String INVALID_CONFIGURATION_FILE_NAME = "Invalid configuration file name.";

    private JSONObject mConfigurationJson;
    private JSONObject mProjectInfoJson;
    private JSONObject mClientJson;

    public GoogleServicesConfigurationConverter(Context context, String configName) {
        mConfigurationJson = loadJsonFromAssets(context, configName);
        initJsons(configName);
    }

    private void initJsons(String configName) {
        try {
            mProjectInfoJson = getJsonObject(mConfigurationJson, configName,
                    PushNotificationsConfigurationKeys.PROJECT_INFO.getKey());
            mClientJson = getFirstJsonFromArray(mConfigurationJson, configName,
                    PushNotificationsConfigurationKeys.CLIENT.getKey());
        } catch (InvalidConfigurationException e) {
            ApiLoggerResolver.logError(getClass().getSimpleName(), e.getLocalizedMessage());
        }
    }

    private JSONObject loadJsonFromAssets(Context context, String configName) {
        if (configName.isEmpty()) {
            try {
                throw new InvalidConfigurationException(INVALID_CONFIGURATION_FILE_NAME);
            } catch (InvalidConfigurationException e) {
                ApiLoggerResolver.logError(getClass().getSimpleName(), e.getLocalizedMessage());
            }
        } else {
            try {
                InputStream is = context.getAssets().open(configName);
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                String jsonString = new String(buffer, StandardCharsets.UTF_8);
                if (jsonString.isEmpty()) {
                    throw new InvalidConfigurationException(EMPTY_CONFIGURATION_EXCEPTION);
                } else {
                    return new JSONObject(jsonString);
                }
            } catch (IOException | JSONException ex) {
                ex.printStackTrace();
            } catch (InvalidConfigurationException ex) {
                ApiLoggerResolver.logError(getClass().getSimpleName(), ex.getLocalizedMessage());
            }
        }
        return null;
    }

    @Nullable
    public String getString(JSONObject json, String jsonName, String key) throws InvalidConfigurationException {
        if (json != null) {
            String value = json.optString(key);
            value = value.trim();
            if (TextUtils.isEmpty(value)) {
                throw new InvalidConfigurationException(key + " field is required but not specified in the " + jsonName);
            }
            return value;
        } else {
            throw new InvalidConfigurationException("Could not find " + jsonName + "json");
        }
    }

    private JSONObject getJsonObject(JSONObject json, String jsonName, String key) throws InvalidConfigurationException {
        if (json != null) {
            try {
                return json.getJSONObject(key);
            } catch (JSONException e) {
                throw new InvalidConfigurationException(key + " json could not be found in the " + jsonName);
            }
        } else {
            throw new InvalidConfigurationException("Could not find " + jsonName + " json");
        }
    }

    private JSONObject getFirstJsonFromArray(JSONObject jsonObject, String jsonName, String key) throws InvalidConfigurationException {
        if (jsonObject != null) {
            JSONArray clientsArray;
            try {
                clientsArray = jsonObject.getJSONArray(key);
                return clientsArray.getJSONObject(0);
            } catch (JSONException e) {
                throw new InvalidConfigurationException(key + " json could not be found in the " + jsonName);
            }
        } else {
            throw new InvalidConfigurationException("Could not find " + jsonName + " json");
        }
    }

    public String getApiKey() throws InvalidConfigurationException {
        return getString(getFirstJsonFromArray(mClientJson,
                PushNotificationsConfigurationKeys.CLIENT.getKey(),
                PushNotificationsConfigurationKeys.API_KEY.getKey()),
                PushNotificationsConfigurationKeys.API_KEY.getKey(),
                PushNotificationsConfigurationKeys.CURRENT_KEY.getKey());
    }

    public String getProjectId() throws InvalidConfigurationException {
        return getString(mProjectInfoJson,
                PushNotificationsConfigurationKeys.PROJECT_INFO.getKey(),
                PushNotificationsConfigurationKeys.PROJECT_ID.getKey());
    }

    public String getDatabaseUrl() throws InvalidConfigurationException {
        return getString(mProjectInfoJson, PushNotificationsConfigurationKeys.PROJECT_INFO.getKey(),
                PushNotificationsConfigurationKeys.DATABASE_URL.getKey());
    }

    public String getApplicationId() throws InvalidConfigurationException {
        return getString(getJsonObject(mClientJson, PushNotificationsConfigurationKeys.PROJECT_INFO.getKey(),
                PushNotificationsConfigurationKeys.CLIENT_INFO.getKey()),
                PushNotificationsConfigurationKeys.CLIENT_INFO.getKey(),
                PushNotificationsConfigurationKeys.APPLICATION_ID.getKey());
    }

    public String getGcmSenderId() throws InvalidConfigurationException {
        return getString(mProjectInfoJson, PushNotificationsConfigurationKeys.PROJECT_INFO.getKey(),
                PushNotificationsConfigurationKeys.GCM_SENDER_ID.getKey());
    }

    public String getStorageBucket() throws InvalidConfigurationException {
        return getString(mProjectInfoJson, PushNotificationsConfigurationKeys.PROJECT_INFO.getKey(),
                PushNotificationsConfigurationKeys.STORAGE_BUCKET.getKey());
    }
}
