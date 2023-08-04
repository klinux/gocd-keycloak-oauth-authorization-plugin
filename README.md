# Keycloak OAuth plugin for GoCD

The plugin allows user to login in GoCD using an Keycloak account. It is implemented using [GoCD authorization endpoint](https://plugin-api.gocd.org/current/authorization/).

Starting with release 1.1.0, this plugin will work by default with Keycloak >= 17.0.0 and GOCD >= 20.3.0

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
