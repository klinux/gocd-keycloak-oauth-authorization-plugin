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

package cd.go.authorization.keycloak;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.math.BigInteger;
import java.util.List;

import static cd.go.authorization.keycloak.utils.Util.GSON;

public class KeycloakIntrospectToken {
    @Expose
    @SerializedName("exp")
    private BigInteger exp;

    @Expose
    @SerializedName("aud")
    private String aud;

    @Expose
    @SerializedName("active")
    private boolean active;

    KeycloakIntrospectToken() {
    }

    public BigInteger getExp() {
        return exp;
    }

    public boolean getActive() {
        return active;
    }

    public String getAudience() {
        return aud;
    }

    public String toJSON() {
        return GSON.toJson(this);
    }

    public static KeycloakIntrospectToken fromJSON(String json) {
        return GSON.fromJson(json, KeycloakIntrospectToken.class);
    }
}
