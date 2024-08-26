# WoDT Digital Twins Platform

![Release](https://github.com/Web-of-Digital-Twins/wodt-platform/actions/workflows/build-and-deploy.yml/badge.svg?style=plastic)
[![License: Apache License](https://img.shields.io/badge/License-Apache_License_2.0-yellow.svg)](https://www.apache.org/licenses/LICENSE-2.0)
![Version](https://img.shields.io/github/v/release/Web-of-Digital-Twins/wodt-platform?style=plastic)

This is an implementation of the WoDT Digital Twins Platform.

## Usage
You need to specify the following environment variable:
- `EXPOSED_PORT`: the port where the WoDT Digital Twins Platform expose its services

If you want to run it via docker container:
1. Provide a `.env` file with all the environment variable described above
2. Run the container with the command:
   ```bash
    docker run ghcr.io/web-of-digital-twins/wodt-platform:<version>
    ```
   1. Provide a port mapping to the exposed port.
   2. If you want to pass an environment file whose name is different from `.env` use the `--env-file <name>` parameter.

## Documentation
- Check out the website [here](https://web-of-digital-twins.github.io/wodt-platform/)
- Direct link to the *Code* documentation [here](https://web-of-digital-twins.github.io/wodt-platform/documentation/code-doc/)
- Direct link to the *REST-API* documentation [here](https://web-of-digital-twins.github.io/wodt-platform/documentation/openapi-doc/)

## Previous versions
Older versions of this work are archived [here](https://github.com/WebBased-WoDT/wodt-dts-platform).
