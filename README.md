# Keycloak OAuth plugin for GoCD

The plugin allows user to login in GoCD using an Keycloak account. It is implemented using [GoCD authorization endpoint](https://plugin-api.gocd.org/current/authorization/).

# Requirements

| Plugin Version | Keycloak Version | Note |
|----------------|------------------|------|
| 3.x | 17.0.0+ (Quarkus) | `/auth` context path removed by default |
| 2.x and below | 16.x and below (WildFly) | `/auth` context path included |

> **Note:** Starting from Keycloak 17.0.0, the default context path `/auth` was removed as part of the migration from WildFly to Quarkus. If you are using Keycloak 17+ with legacy `/auth` path configured via `--http-relative-path /auth`, you may need to use plugin version 2.x or adjust your Keycloak endpoint accordingly.

# Installation

Installation documentation available [here](docs/INSTALL.md).

# Capabilities

* Currently supports authentication and authorization capabilities.

## Building the code base

To build the jar, run `./gradlew clean test assemble`

### Information about this plugin
This plugin was created based on [okta-oauth-authorization-plugin](https://github.com/szamfirov/gocd-okta-oauth-authorization-plugin)

## License

```plain
Copyright 2017 ThoughtWorks, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
