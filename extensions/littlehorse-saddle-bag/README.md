# LittleHorse Quarkus Saddle Bag Extension

The standard extension for packaging [LittleHorse](https://littlehorse.io/) task workers as distributable Docker images with Quarkus.

The Saddle Bag extension scans your `@LHTask` classes and `@LHStructDef` structs at build time and produces a
manifest (JSON/YAML/PROPERTIES) describing all tasks, their inputs/outputs, struct definitions, and required configurations.
It also establishes the standard structure for building saddle bag Docker images — self-contained, ready-to-deploy
task worker containers that include the manifest alongside the application.

This manifest can be used for documentation, service catalogs, deployment tooling, or runtime discovery of
what a task worker image provides.

# Table of Content

<!-- TOC -->
* [LittleHorse Quarkus Saddle Bag Extension](#littlehorse-quarkus-saddle-bag-extension)
* [Table of Content](#table-of-content)
* [Installation](#installation)
* [Usage](#usage)
  * [Basic Setup](#basic-setup)
  * [Declaring Required Configurations](#declaring-required-configurations)
* [Generated Output](#generated-output)
* [Configurations](#configurations)
  * [Bag Configurations](#bag-configurations)
  * [Metadata Configurations](#metadata-configurations)
  * [Output Configurations](#output-configurations)
<!-- TOC -->

# Installation

<a href="https://central.sonatype.com/artifact/io.littlehorse/littlehorse-quarkus-saddle-bag"><img alt="Maven Central" src="https://img.shields.io/maven-central/v/io.littlehorse/littlehorse-quarkus-saddle-bag?label=latest"></a>

This extension is available at [Maven Central](https://central.sonatype.com/artifact/io.littlehorse/littlehorse-quarkus-saddle-bag).

Gradle:

```groovy
implementation "io.littlehorse:littlehorse-quarkus-saddle-bag:${lhVersion}"
```

Maven:

```xml
<dependency>
    <groupId>io.littlehorse</groupId>
    <artifactId>littlehorse-quarkus-saddle-bag</artifactId>
    <version>${lhVersion}</version>
</dependency>
```

> **Note:** This extension requires the base [LittleHorse Quarkus](../littlehorse-quarkus) extension.

# Usage

## Basic Setup

Add the required configuration to your `application.properties`:

```properties
# Bag metadata
quarkus.littlehorse.saddle.bag.name=my-service
quarkus.littlehorse.saddle.bag.title=My Service
quarkus.littlehorse.saddle.bag.author=Example Team
quarkus.littlehorse.saddle.bag.description=A task worker service for order processing
quarkus.littlehorse.saddle.bag.metadata.tags=orders,processing
quarkus.littlehorse.saddle.bag.metadata.licence=Apache-2.0
quarkus.littlehorse.saddle.bag.metadata.documentation-url=https://example.com/docs
quarkus.littlehorse.saddle.bag.metadata.icon-url=https://example.com/icon.png
quarkus.littlehorse.saddle.bag.metadata.support-email=support@example.com
```

The extension automatically scans all `@LHTask` classes and `@LHStructDef` structs, generating a manifest during the Quarkus build augmentation phase.

> **Important:** Task and struct names must use configuration expressions (e.g., `${task.my-task.name}`).
> This allows the saddle bag to track which configuration key maps to each task/struct name.

```java
@LHTask
public class OrderTask {

    public static final String PROCESS_ORDER = "${task.process-order.name}";

    @LHTaskMethod(value = PROCESS_ORDER, description = "Processes an incoming order")
    public String processOrder(String orderId) {
        return "Processed: " + orderId;
    }
}
```

With the corresponding property:

```properties
task.process-order.name=process-order
```

## Declaring Required Configurations

Use the `@LHTaskConfig` annotation to declare external configuration properties that your task worker requires at runtime.
This is useful when your task depends on external services (APIs, databases, message brokers, etc.) and consumers
of the saddle bag need to know which configurations to provide.

```java
@LHTask
@LHTaskConfig(value = "smtp.host", description = "SMTP server hostname")
@LHTaskConfig(value = "smtp.port", description = "SMTP server port", defaultValue = "587")
@LHTaskConfig(value = "smtp.password", description = "SMTP password", sensitive = true)
public class EmailNotificationTask {

    @LHTaskMethod(value = "${task.send-email.name}", description = "Sends an email notification")
    public void sendEmail(String recipient, String subject, String body) {
        // ...
    }
}
```

### `@LHTaskConfig` Attributes

| Attribute      | Type      | Default | Description                                                        |
|----------------|-----------|---------|--------------------------------------------------------------------|
| `value`        | `String`  | —       | The configuration property key (required)                          |
| `description`  | `String`  | `""`    | Human-readable description of the property                         |
| `sensitive`    | `boolean` | `false` | Whether the value is sensitive (passwords, API keys, etc.)         |
| `defaultValue` | `String`  | `""`    | Default value; empty means the property is mandatory               |

The declared configurations appear in the generated manifest under the `configs` field for each task.

# Generated Output

The extension generates:

1. **JAR resource** at `META-INF/saddle-bag/saddle-bag.json` (always generated)
2. **Output file** at `build/saddle-bag/saddle-bag.yaml` (configurable format and path)

Example output (`saddle-bag.yaml`):

```yaml
name: "my-service"
title: "My Service"
author: "Example Team"
description: "A task worker service for order processing"
version: "1.0.0"
metadata:
  tags:
  - "orders"
  - "processing"
  licence: "Apache-2.0"
  documentation-url: "https://example.com/docs"
  icon-url: "https://example.com/icon.png"
  support-email: "support@example.com"
tasks:
  send-email:
    inputs:
    - name: "recipient"
      type: "STR"
    - name: "subject"
      type: "STR"
    - name: "body"
      type: "STR"
    configName: "task.send-email.name"
    description: "Sends an email notification"
    configs:
    - key: "smtp.host"
      description: "SMTP server hostname"
      sensitive: false
    - key: "smtp.port"
      description: "SMTP server port"
      sensitive: false
      defaultValue: "587"
    - key: "smtp.password"
      description: "SMTP password"
      sensitive: true
structs: {}
```

# Configurations

## Bag Configurations

``quarkus.littlehorse.saddle.bag.name``
The name of the saddle bag (identifies the service/package).

* Type: string
* Importance: high

``quarkus.littlehorse.saddle.bag.title``
A human-readable title for the saddle bag.

* Type: string
* Importance: high

``quarkus.littlehorse.saddle.bag.author``
The author or owning team for the saddle bag.

* Type: string
* Importance: high

``quarkus.littlehorse.saddle.bag.description``
A description of what this saddle bag provides.

* Type: string
* Importance: high

## Metadata Configurations

``quarkus.littlehorse.saddle.bag.metadata.tags``
Comma-separated list of tags for categorization.

* Type: list of strings
* Importance: medium

``quarkus.littlehorse.saddle.bag.metadata.licence``
The license under which this saddle bag is distributed.

* Type: string
* Importance: medium

``quarkus.littlehorse.saddle.bag.metadata.documentation-url``
URL to the documentation for this saddle bag.

* Type: string
* Importance: low

``quarkus.littlehorse.saddle.bag.metadata.icon-url``
URL to an icon representing this saddle bag.

* Type: string
* Importance: low

``quarkus.littlehorse.saddle.bag.metadata.support-email``
Support contact email.

* Type: string
* Importance: low

## Output Configurations

``quarkus.littlehorse.saddle.bag.output.enable``
Whether to generate the output file in the build directory.

* Type: boolean
* Default: `true`
* Importance: medium

``quarkus.littlehorse.saddle.bag.output.path``
Relative path (from the build output directory) where the file is generated.

* Type: string
* Default: `saddle-bag/`
* Importance: low

``quarkus.littlehorse.saddle.bag.output.filename``
Name of the generated file (without extension).

* Type: string
* Default: `saddle-bag`
* Importance: low

``quarkus.littlehorse.saddle.bag.output.format``
Output format for the generated file.

* Type: string
* Default: `yaml`
* Valid values: `json`, `yaml`, `properties`
* Importance: low
