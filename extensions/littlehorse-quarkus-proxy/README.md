# LittleHorse Quarkus Proxy Extension

This is RESTful reverse proxy for [LittleHorse](https://littlehorse.io/).

# Table of Content

<!-- TOC -->
* [LittleHorse Quarkus Proxy Extension](#littlehorse-quarkus-proxy-extension)
* [Table of Content](#table-of-content)
* [Installation](#installation)
* [Configurations](#configurations)
  * [Passing Configurations](#passing-configurations)
  * [LittleHorse Client Configurations](#littlehorse-client-configurations)
<!-- TOC -->

# Installation

<a href="https://central.sonatype.com/artifact/io.littlehorse/littlehorse-quarkus-proxy"><img alt="Maven Central" src="https://img.shields.io/maven-central/v/io.littlehorse/littlehorse-quarkus-proxy?label=latest"></a>

This extension is available at [Maven Central](https://central.sonatype.com/artifact/io.littlehorse/littlehorse-quarkus).

Gradle:

```groovy
implementation "io.littlehorse:littlehorse-quarkus-proxy:${lhVersion}"
```

Maven:

```xml
<dependency>
    <groupId>io.littlehorse</groupId>
    <artifactId>littlehorse-quarkus-proxy</artifactId>
    <version>${lhVersion}</version>
</dependency>
```

# Configurations

More about configurations at: [Configuration Reference Guide](https://quarkus.io/guides/config-reference).

## Passing Configurations

Quarkus supports multiple configuration sources.

Some examples could be:

* **Environment Variables:** Adding a variable (ex: `LHC_API_PORT=2023`) to the OS/Container.
* **System Properties:** Adding a `-D` property (ex: `-Dlhc.api.port=2023`) to the command line when running the
  artifact.
* **Property File:** Adding a property entry (ex: `lhc.api.port=2023`) into the `application.properties` file.

## LittleHorse Client Configurations

More about LH Configurations at [Clients Configurations](https://littlehorse.io/docs/server/operations/client-configuration).

``lhc.api.host``
The bootstrap host for the LittleHorse Server.

* Type: string
* Importance: high

``lhc.api.port``
The bootstrap port for the LittleHorse Server.

* Type: int
* Importance: high

``lhc.api.protocol``
The bootstrap protocol for the LittleHorse Server.

* Type: string
* Default: PLAINTEXT
* Valid Values: [PLAINTEXT, TLS]
* Importance: high

``lhc.tenant.id``
Tenant ID which represents a logically isolated environment within LittleHorse.

* Type: string
* Default: default
* Importance: medium

``lhc.ca.cert``
Optional location of CA Cert file that issued the server side certificates. For TLS and mTLS connection.

* Type: string
* Default: null
* Importance: low

``lhc.client.cert``
Optional location of Client Cert file for mTLS connection.

* Type: string
* Default: null
* Importance: low

``lhc.client.key``
Optional location of Client Private Key file for mTLS connection.

* Type: string
* Default: null
* Importance: low

``lhc.grpc.keepalive.time.ms``
Time in milliseconds to configure keepalive pings on the grpc client.

* Type: long
* Default: 45000 (45 seconds)
* Importance: low

``lhc.grpc.keepalive.timeout.ms``
Time in milliseconds to configure the timeout for the keepalive pings on the grpc client.

* Type: long
* Default: 5000 (5 seconds)
* Importance: low

``lhc.oauth.access.token.url``
Optional Access Token URL provided by the OAuth Authorization Server. Used by the Worker to obtain a token using client credentials flow.

* Type: string
* Default: null
* Importance: low

``lhc.oauth.client.id``
Optional OAuth2 Client Id. Used by the Worker to identify itself at an Authorization Server. Client Credentials Flow.

* Type: string
* Default: null
* Importance: low

``lhc.oauth.client.secret``
Optional OAuth2 Client Secret. Used by the Worker to identify itself at an Authorization Server. Client Credentials Flow.

* Type: password
* Default: null
* Importance: low
