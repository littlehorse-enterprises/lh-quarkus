# LittleHorse Extension for Quarkus

<a href="https://github.com/littlehorse-enterprises/lh-quarkus"><img alt="github" src="https://img.shields.io/badge/GitHub-blue?logo=github&logoColor=white"></a>
<a href="https://quarkus.io/"><img alt="quarkus" src="https://img.shields.io/badge/Quarkus-ff004a?logo=quarkus&logoColor=white"/></a>
<a href="https://littlehorse.io/"><img alt="littlehorse" src="https://raw.githubusercontent.com/littlehorse-enterprises/littlehorse/refs/heads/master/img/badges/gray.svg"/></a>

## Table of Content

<!-- TOC -->
* [LittleHorse Extension for Quarkus](#littlehorse-extension-for-quarkus)
  * [Table of Content](#table-of-content)
  * [Installation](#installation)
  * [Versioning](#versioning)
  * [Interoperability Table](#interoperability-table)
  * [Usage](#usage)
  * [Configurations](#configurations)
  * [Example Project](#example-project)
  * [Development](#development)
  * [License](#license)
<!-- TOC -->

## Installation

<img alt="Maven Central" src="https://img.shields.io/maven-central/v/io.littlehorse/littlehorse-quarkus?label=latest">

This extension is available at [Maven Central](https://central.sonatype.com/artifact/io.littlehorse/littlehorse-quarkus).

Gradle:

```groovy
implementation "io.littlehorse:littlehorse-quarkus:${version}"
```

Maven:

```xml
<dependency>
    <groupId>io.littlehorse</groupId>
    <artifactId>littlehorse-quarkus</artifactId>
    <version>{version}</version>
</dependency>
```

## Versioning

We use [Semantic Versioning](https://semver.org/spec/v2.0.0.html)
where `major.minor` numbers indicate littlehorse version compatibility,
and the `patch` digit indicates the extension version.

- `major` LittleHorse Server `major` version compatibility.
- `minor` LittleHorse Server `minor` version compatibility.
- `patch` **LittleHorse Extension** bundle version.

## Interoperability Table

| LittleHorse Version | Quarkus Version |
|---------------------|-----------------|
| `0.13.*`            | `3.22.*`        |

## Usage

For instructions about how to use this extension go to [USAGE.md](USAGE.md).

## Configurations

For LittleHorse and LittleHorse Extension configurations go to [CONFIGURATIONS.md](CONFIGURATIONS.md).

## Example Project

For an example project go to the [example](example) folder.

## Development

For development instructions go to [DEVELOPMENT.md](DEVELOPMENT.md).

## License

<a href="https://www.gnu.org/licenses/agpl-3.0.en.html"><img alt="AGPLv3 License" src="https://img.shields.io/badge/covered%20by-AGPLv3-blue"></a>

All code in this repository is licensed by the [GNU Affero General Public License, Version 3](https://www.gnu.org/licenses/agpl-3.0.en.html) and is copyright of LittleHorse Enterprises LLC.
