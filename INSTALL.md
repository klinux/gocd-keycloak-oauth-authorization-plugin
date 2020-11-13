# Keycloak oauth plugin for GoCD

## Requirements

* GoCD server version v17.5.0 or above
* Keycloak [API documentation](https://www.keycloak.org/docs-api/11.0/rest-api/index.html)

## Installation

Copy the file `build/libs/keycloak-oauth-authorization-plugin-VERSION.jar` to the GoCD server under `${GO_SERVER_DIR}/plugins/external` 
and restart the server. The `GO_SERVER_DIR` is usually `/var/lib/go-server` on Linux and `C:\Program Files\Go Server` 
on Windows.

## Configuration

###  Configure Keycloak API Issuer

1. Sign in Keycloak Console
2. Select the realm that you want to configure. Ex. **Master**
3. Click in **Clients** menu 
4. Click **Add** button
5. On the form insert the client name
6. On the next page, set this configs:
    1. In **Access Type** select **Confidential**
    2. In **Valid Redirect URIs** insert the URL of GoCD, ex.: **http://localhost:8153**
    3. In **Credentials** tab copy value of **Secret**

### Create Role Configuration

1. Sign in Keycloak Console
2. Select the realm that you want to configure. Ex. **Master**
3. Click in **Roles** menu
    1. Click **Add Role** button
    2. Insert the name of **Role** and it description
    3. Save the **Role**
    4. Select the user that you want to configure this role
    5. Select **Role Mappings** tab and select tht **Role** created
