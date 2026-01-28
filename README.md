# LittleHorse Extensions for Quarkus

<a href="https://github.com/littlehorse-enterprises/lh-quarkus"><img alt="github" src="https://img.shields.io/badge/GitHub-blueviolet?logo=github&logoColor=white"></a>
<a href="https://quarkus.io/"><img alt="quarkus" src="https://img.shields.io/badge/Quarkus-ff004a?logo=quarkus&logoColor=white"/></a>
<a href="https://littlehorse.io/"><img alt="littlehorse" src="https://raw.githubusercontent.com/littlehorse-enterprises/littlehorse/refs/heads/master/img/badges/blue.svg"/></a>

## Table of Content

<!-- TOC -->
* [LittleHorse Extensions for Quarkus](#littlehorse-extensions-for-quarkus)
  * [Table of Content](#table-of-content)
  * [Examples](#examples)
  * [Extensions](#extensions)
  * [RESTful Gateway](#restful-gateway)
  * [Versioning](#versioning)
  * [Development](#development)
  * [License](#license)
<!-- TOC -->

## Examples

- [Basic Example](examples/basic): How to run a task and register a workflow.
- [Child Workflow](examples/child-workflow): How to build parent and child workflows.
- [Reactive](examples/reactive): How to use `LittleHorseReactiveStub` object.
- [Rest](examples/rest): How to run a wf run request from a rest endpoint.
- [User Tasks](examples/user-tasks): How to use and register user tasks.

## Extensions

<a href="https://central.sonatype.com/search?q=littlehorse-quarkus"><img alt="Maven Central" src="https://img.shields.io/badge/central-orange?logo=sonatype&logoColor=white"></a>

- [LittleHorse Quarkus](extensions/littlehorse-quarkus): It's the base extension which allows you to develop on Quarkus and LittleHorse.
- [LittleHorse Quarkus RESTful Gateway](extensions/littlehorse-quarkus-restful-gateway): RESTful gateway for LittleHorse.

## RESTful Gateway

<a href="https://github.com/orgs/littlehorse-enterprises/packages?repo_name=lh-quarkus"><img alt="GitHub" src="https://img.shields.io/badge/ghcr-orange?logo=docker&logoColor=white"></a>

This gateway allows you to interact with [LittleHorse](https://littlehorse.io/) in a RESTful style.
Check the configurations at [LittleHorse RESTful Gateway](extensions/littlehorse-quarkus-restful-gateway).

```shell
docker pull ghcr.io/littlehorse-enterprises/lh-restful-gateway:latest
```


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

<a href="https://github.com/littlehorse-enterprises/lh-quarkus/blob/main/LICENSE.md"><img alt="Apache-2.0" src="https://img.shields.io/github/license/littlehorse-enterprises/lh-quarkus?label=&logo=apache"></a>

All code in this repository is licensed by the [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0) and is copyright of LittleHorse Enterprises LLC.
