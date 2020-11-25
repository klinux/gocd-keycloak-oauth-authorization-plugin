# Authorization Configuration

The `Authorization Configuration` is used to configure a connection to an Keycloak Authorization server. The configuration is later used by the plugin to authorize a user or search for them in the Keycloak. You can also configure multiple Keycloak servers by creating multiple authorization configurations.

1. Login to the GoCD server as an administrator and navigate to **_Admin_** _>_ **_Security_** _>_ **_Authorization Configuration_**.  
2. Click on **_Add_** to create a new authorization configuration.  
3. Provide a unique identifier for this authorization configuration and select `Keycloak oauth authorization plugin` as the **Plugin**.

4. **Keycloak Endpoint (`Mandatory`):** Specify your Keycloak Endpoint.

    ![Keycloak Endpoint](images/keycloak_endpoint.png?raw=true "Keycloak Endpoint")

    ```xml
      <property>
        <key>KeycloakEndpoint</key>
        <value>https://auth.example.com</value>
      </property>
    ```
   > If you customize the Keycloak endpoint, Ex.: remove /auth context, you need to adapt the plugin.

5. **Keycloak Realm (`Mandatory`):** Specify your Keycloak Realm.

    ![Realm](images/keycloak_realm.png?raw=true "Realm")

    ```xml
      <property>
        <key>KeycloakRealm</key>
        <value>master</value>
      </property>
    ```

6. **Keycloak Client ID  (`Mandatory`):**  Specify your Keycloak Client ID.

    ![Client ID](images/keycloak_clientid.png?raw=true "Client ID")

    ```xml
      <property>
        <key>ClientId</key>
        <value>gocd-agent</value>
      </property>
    ```

7. **Keycloak Client Secret Key  (`Mandatory`):**  Specify your Keycloak Secret Key.

    ![Secret Key](images/keycloak_secretkey.png?raw=true "Secret Key")

    ```xml
      <property>
        <key>ClientSecret</key>
        <encryptedValue>YOUR_SECRET</encryptedValue>
      </property>
    ```

8. Click on **_Check connection_** to verify your configuration. The plugin will establish a connection with `Keycloak server` using the configuration and will return the verification status.

9. Once check connection succeeds, click on **_Save_** to save the authorization configuration.

#### Example authorization configuration

![Authorization configuration](images/keycloak_config.png?raw=true "Authorization configuration")

<hr/>

**Alternatively, the configuration can be added directly to the GoCD config XML using the `<authConfig>` tag.  It  should be added under `<security/>` tag as described in the following example:**

```xml
<security>
    <authConfigs>
        <authConfig id="Keycloak" pluginId="cd.go.authorization.keycloak">
          <property>
            <key>KeycloakEndpoint</key>
            <value>https://auth.example.com</value>
          </property>
          <property>
            <key>KeycloakRealm</key>
            <value>master</value>
          </property>
          <property>
            <key>ClientId</key>
            <value>gocd-agent</value>
          </property>
          <property>
            <key>ClientSecret</key>
            <encryptedValue>your_secret_key</encryptedValue>
          </property>
        </authConfig>
    </authConfigs>
    <admins>
        <user>kleber.rocha@cora.com.br</user>
    </admins>
</security>
```