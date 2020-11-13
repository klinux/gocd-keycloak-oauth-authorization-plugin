/*
 * Copyright 2017 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cd.go.authorization.keycloak.models;

import cd.go.authorization.keycloak.KeycloakApiClient;
import cd.go.authorization.keycloak.annotation.ProfileField;
import cd.go.authorization.keycloak.annotation.Validatable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.util.Map;

import static cd.go.authorization.keycloak.utils.Util.GSON;

public class KeycloakConfiguration implements Validatable {
    @Expose
    @SerializedName("KeycloakEndpoint")
    @ProfileField(key = "KeycloakEndpoint", required = true, secure = false)
    private String keycloakEndpoint;

    @Expose
    @SerializedName("ClientId")
    @ProfileField(key = "ClientId", required = true, secure = false)
    private String clientId;

    @Expose
    @SerializedName("ClientSecret")
    @ProfileField(key = "ClientSecret", required = true, secure = true)
    private String clientSecret;

    private KeycloakApiClient keycloakApiClient;

    public KeycloakConfiguration() {
    }

    public KeycloakConfiguration(String keycloakEndpoint, String clientId, String clientSecret) {
        this.keycloakEndpoint = keycloakEndpoint;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public String keycloakEndpoint() {
        return keycloakEndpoint;
    }

    public String clientId() {
        return clientId;
    }

    public String clientSecret() {
        return clientSecret;
    }

    public String toJSON() {
        return GSON.toJson(this);
    }

    public static KeycloakConfiguration fromJSON(String json) {
        return GSON.fromJson(json, KeycloakConfiguration.class);
    }

    public Map<String, String> toProperties() {
        return GSON.fromJson(toJSON(), new TypeToken<Map<String, String>>() {
        }.getType());
    }

    public KeycloakApiClient keycloakApiClient() {
        if (keycloakApiClient == null) {
            keycloakApiClient = new KeycloakApiClient(this);
        }

        return keycloakApiClient;
    }
}
