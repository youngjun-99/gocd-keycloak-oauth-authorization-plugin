# Keycloak oauth plugin for GoCD

## Requirements

* GoCD server version17.5.0 or above
* Keycloak version requirements:

| Plugin Version | Keycloak Version | Note |
|----------------|------------------|------|
| 3.x | 17.0.0+ (Quarkus) | `/auth` context path removed by default |
| 2.x and below | 16.x and below (WildFly) | `/auth` context path included |

> **Important:** Keycloak 17.0.0 migrated from WildFly to Quarkus and removed the default `/auth` context path. Plugin 3.x is designed for Keycloak 17+ without the `/auth` path.
>
> If you're using Keycloak 17+ but have configured `--http-relative-path /auth` to keep the legacy path, use plugin version 2.x instead.

* Keycloak [API documentation](https://www.keycloak.org/docs/latest/server_admin/)

## Installation

Copy the file `build/libs/keycloak-oauth-authorization-plugin-VERSION.jar` to the GoCD server under `${GO_SERVER_DIR}/plugins/external` 
and restart the server. The `GO_SERVER_DIR` is usually `/var/lib/go-server` on Linux and `C:\Program Files\Go Server` 
on Windows.

## Configuration
Provide details of the Keycloak server to connect to via an [Authorization Configuration](AUTHORIZATION_CONFIGURATION.md).

###  Configure Keycloak Client

1. Sign in Keycloak Console
2. Select the realm that you want to configure. Ex. **Master**
3. Click in **Clients** menu 
4. Click **Add** button
5. On the form insert the client name
6. On the next page, set these configs:
    1. In **Access Type** select **Confidential**
    2. In **Valid Redirect URIs** insert the URL of GoCD, ex.: **http://localhost:8153**
    3. In **Credentials** tab copy value of **Secret**

### Create Group Configuration

1. Sign in Keycloak Console
2. Select the realm that you want to configure. Ex. **Master**
3. Click in **Groups** menu
    1. Click **Add Group** button
    2. Insert the name of **Group** and the description
    3. Save the **Group**
    4. Select the **user** that you want to configure this role
    5. Select **Groups** tab and select the group in **Available Groups**

> Obs.: By default Keycloak do not provide group definition on user session, to get this, edit 
>**profile** scope and add groups in **Mappers** tab, the scope needs to be added as builtin.
