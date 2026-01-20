# LittleHorse Extensions for Quarkus

<a href="https://github.com/littlehorse-enterprises/lh-quarkus"><img alt="github" src="https://img.shields.io/badge/GitHub-blue?logo=github&logoColor=white"></a>
<a href="https://quarkus.io/"><img alt="quarkus" src="https://img.shields.io/badge/Quarkus-ff004a?logo=quarkus&logoColor=white"/></a>
<a href="https://littlehorse.io/"><img alt="littlehorse" src="https://raw.githubusercontent.com/littlehorse-enterprises/littlehorse/refs/heads/master/img/badges/gray.svg"/></a>

## Table of Content

<!-- TOC -->
* [LittleHorse Extensions for Quarkus](#littlehorse-extensions-for-quarkus)
  * [Table of Content](#table-of-content)
  * [Extensions](#extensions)
  * [Examples](#examples)
  * [Versioning](#versioning)
  * [Development](#development)
  * [License](#license)
<!-- TOC -->

## Extensions

- [LittleHorse Quarkus](extensions/littlehorse-quarkus): It's the base extension which allows you to develop on Quarkus and LittleHorse.
- [LittleHorse Quarkus Proxy](extensions/littlehorse-quarkus-proxy): RESTful reverse proxy for LittleHorse.

## Examples

- [Basic Example](examples/basic): How to run a task and register a workflow.
- [Integration Tests](examples/integration-tests): How to perform integration tests with LH Testcontainers.
- [Reactive](examples/reactive): How to use `LittleHorseReactiveStub` object.
- [Rest](examples/rest): How to run a wf run request from a rest endpoint.
- [User Tasks](examples/user-tasks): How to use and register user tasks.
- [Child Workflow](examples/child-workflow): How to build parent and child workflows.
- [Proxy](examples/proxy): How to configure LH Quarkus Proxy Extension.

## Versioning

These extensions follow the same versioning conventions as [LittleHorse](https://github.com/littlehorse-enterprises/littlehorse/releases).

| LittleHorse Extension Version | Quarkus Version |
|-------------------------------|-----------------|
| `0.14.*`                      | `3.24.*`        |
| `0.15.*`                      | `3.29.*`        |
| `0.16.*`                      | `3.30.*`        |

## Development

For development instructions go to [DEVELOPMENT.md](DEVELOPMENT.md).

## License

<a href="https://github.com/littlehorse-enterprises/lh-quarkus/blob/main/LICENSE.md"><img alt="Apache-2.0" src="https://img.shields.io/github/license/littlehorse-enterprises/lh-quarkus?label=covered%20by"></a>

All code in this repository is licensed by the [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0) and is copyright of LittleHorse Enterprises LLC.
