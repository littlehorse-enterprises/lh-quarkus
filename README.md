# LittleHorse Extension for Quarkus

<a href="https://github.com/littlehorse-enterprises/lh-quarkus"><img alt="github" src="https://img.shields.io/badge/GitHub-blue?logo=github&logoColor=white"></a>
<a href="https://quarkus.io/"><img alt="quarkus" src="https://img.shields.io/badge/Quarkus-ff004a?logo=quarkus&logoColor=white"/></a>
<a href="https://littlehorse.io/"><img alt="littlehorse" src="https://raw.githubusercontent.com/littlehorse-enterprises/littlehorse/refs/heads/master/img/badges/gray.svg"/></a>

## Table of Content

<!-- TOC -->
* [LittleHorse Extension for Quarkus](#littlehorse-extension-for-quarkus)
  * [Table of Content](#table-of-content)
  * [Features](#features)
  * [Installation](#installation)
  * [Versioning](#versioning)
  * [Interoperability Table](#interoperability-table)
  * [Usage](#usage)
  * [Configurations](#configurations)
  * [Example Project](#example-project)
  * [Development](#development)
  * [Troubleshooting](#troubleshooting)
  * [License](#license)
<!-- TOC -->

## Features

- Register and start tasks
- Register workflows
- Register user tasks
- Health checks
- Default LH beans
- Native build support

## Installation

<a href="https://central.sonatype.com/artifact/io.littlehorse/littlehorse-quarkus"><img alt="Maven Central" src="https://img.shields.io/maven-central/v/io.littlehorse/littlehorse-quarkus?label=latest"></a>

This extension is available at [Maven Central](https://central.sonatype.com/artifact/io.littlehorse/littlehorse-quarkus).

Gradle:

```groovy
implementation "io.littlehorse:littlehorse-quarkus:${lhVersion}"
```

Maven:

```xml
<dependency>
    <groupId>io.littlehorse</groupId>
    <artifactId>littlehorse-quarkus</artifactId>
    <version>${lhVersion}</version>
</dependency>
```

## Versioning

This extension keeps the same versioning as [LittleHorse](https://github.com/littlehorse-enterprises/littlehorse/releases).

## Interoperability Table

| LittleHorse Extension Version | Quarkus Version |
|-------------------------------|-----------------|
| `0.14.*`                      | `3.24.*`        |

## Usage

For instructions about how to use this extension go to [USAGE.md](USAGE.md).

## Configurations

For LittleHorse and LittleHorse Extension configurations go to [CONFIGURATIONS.md](CONFIGURATIONS.md).

## Example Project

For an example project go to the [src](src) folder.

## Development

For development instructions go to [DEVELOPMENT.md](DEVELOPMENT.md).

## Troubleshooting

For solving common problems go to [TROUBLESHOOTING.md](TROUBLESHOOTING.md).

## License

<a href="https://github.com/littlehorse-enterprises/lh-quarkus/blob/main/LICENSE.md"><img alt="Apache-2.0" src="https://img.shields.io/github/license/littlehorse-enterprises/lh-quarkus?label=covered%20by"></a>

All code in this repository is licensed by the [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0) and is copyright of LittleHorse Enterprises LLC.
