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
    * [Deduplication and Validation](#deduplication-and-validation)
    * [`@LHTaskConfig` Attributes](#lhtaskconfig-attributes)
  * [Declaring Business Exceptions](#declaring-business-exceptions)
    * [`@LHThrownException` Attributes](#lhthrownexception-attributes)
* [Generated Output](#generated-output)
  * [Type Representation](#type-representation)
* [Building a Docker Image](#building-a-docker-image)
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
quarkus.littlehorse.saddle.bag.metadata.docker-image=ghcr.io/example-org/order-service
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

Configurations describe external properties (API URLs, credentials, service endpoints, etc.) that a
task worker requires at runtime. The `@LHTaskConfig` annotation declares them, and its scope depends
on where it is placed:

- On an `@LHTask` **class** — emitted as a **global** saddle-bag config under the top-level `configs`
  field (same level as `tasks`).
- On an `@LHTaskMethod` **method** — emitted under the `configs` field of that specific task.

```java
@LHTask
@LHTaskConfig(value = "smtp.host", description = "SMTP server hostname", type = LHTaskConfigType.STR)
@LHTaskConfig(value = "smtp.password", description = "SMTP password", sensitive = true, type = LHTaskConfigType.STR)
public class EmailNotificationTask {

    @LHTaskMethod(value = "${task.send-email.name}", description = "Sends an email notification")
    @LHTaskConfig(value = "email.send.max-retries", description = "Max delivery attempts", defaultValue = "3", type = LHTaskConfigType.INT)
    public void sendEmail(String recipient, String subject, String body) {
        // ...
    }
}
```

### Deduplication and Validation

- If two `@LHTask` classes declare the same class-level `@LHTaskConfig` key, it is emitted **once** in
  the global `configs` (duplicates are collapsed).
- If a single `@LHTaskMethod` declares the same method-level `@LHTaskConfig` key more than once, it is
  emitted **once** for that task.
- If two **different** `@LHTaskMethod`s declare the same method-level `@LHTaskConfig` key, the build
  **fails**. Shared configuration must instead be declared once at the class level so it becomes a
  global saddle-bag config.

### `@LHTaskConfig` Attributes

The annotation supports the same attributes on both classes and methods:

| Attribute      | Type      | Default | Description                                                        |
|----------------|-----------|---------|--------------------------------------------------------------------|
| `value`        | `String`     | —       | The configuration property key (required)                          |
| `description`  | `String`     | `""`    | Human-readable description of the property                         |
| `sensitive`    | `boolean`    | `false` | Whether the value is sensitive (passwords, API keys, etc.)         |
| `defaultValue` | `String`     | `""`    | Default value; empty means the property is mandatory               |
| `type`         | `LHTaskConfigType` | —       | Value type for validation: `STR`, `INT`, `DOUBLE`, or `BOOL` (required) |

Global configs appear under the top-level `configs` field; method-level configs appear under the
`configs` field of the owning task.


## Declaring Business Exceptions

Use the `@LHThrownException` annotation on an `@LHTaskMethod` to declare the business `EXCEPTION`s that the
task may throw. Business exceptions are thrown from your task code via `LHTaskException` (or a subclass) and can be
caught by a `WfSpec`. Declaring them lets consumers of the saddle bag know which exceptions to handle. See the
[exception handling docs](https://littlehorse.io/docs/server/concepts/exception-handling) for more details.

```java
@LHTask
public class PaymentTask {

    @LHTaskMethod(value = "${task.charge-credit-card.name}", description = "Charges a credit card")
    @LHThrownException(name = "insufficient-funds", description = "Card balance is too low")
    @LHThrownException(name = "amount-too-large", description = "Charge exceeds $10,000")
    public void chargeCreditCard(String userId, double amount) {
        if (amount > 10000) {
            throw new LHTaskException("amount-too-large", "Cannot charge more than $10,000");
        }
        // ...
    }
}
```

### `@LHThrownException` Attributes

| Attribute     | Type     | Default | Description                                                            |
|---------------|----------|---------|-----------------------------------------------------------------------|
| `name`        | `String` | —       | The business exception name in kebab-case (required)                  |
| `description` | `String` | `""`    | Human-readable description of when and why the exception is thrown     |

The declared exceptions appear in the generated manifest under the `exceptions` field for each task.

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
  docker-image: "ghcr.io/example-org/order-service"
tasks:
  process-order:
    output:
      type:
        primitive: "STR"
    inputs:
    - name: "order"
      type:
        struct: "order"
    config-name: "task.process-order.name"
    description: "Processes an incoming order and returns a confirmation"
    configs:
    - key: "orders.process.max-retries"
      description: "Max processing attempts"
      sensitive: false
      type:
        primitive: "INT"
      default-value: "3"
structs:
  order:
    config-name: "struct.order.name"
    description: "A customer order"
    properties:
    - name: "productName"
      type:
        primitive: "STR"
    - name: "quantity"
      type:
        primitive: "INT"
configs:
- key: "orders.service.url"
  description: "Orders service base URL"
  sensitive: false
  type:
    primitive: "STR"
- key: "orders.service.timeout-ms"
  description: "Orders service request timeout"
  sensitive: false
  type:
    primitive: "INT"
  default-value: "5000"
- key: "orders.service.api-key"
  description: "Orders service API key"
  sensitive: true
  type:
    primitive: "STR"
```

## Type Representation

Every task input, task output, struct property, and task config has a `type` field whose value is a
**type descriptor object**. Exactly one of the following keys is present, and its presence identifies
the kind of type (a `oneof`, mirroring the LittleHorse `TypeDefinition`):

| Key         | Meaning                                                                                  |
|-------------|------------------------------------------------------------------------------------------|
| `primitive` | A primitive `VariableType` name: `STR`, `INT`, `DOUBLE`, `BOOL`, `BYTES`, `TIMESTAMP`, `WF_RUN_ID`, `JSON_OBJ`, `JSON_ARR`. |
| `struct`    | The referenced `@LHStructDef` struct's resolved name (a key under the top-level `structs` section). |
| `array`     | An object with an `elements` field holding a `type` descriptor.                          |
| `map`       | An object with `key` and `value` fields, each holding a `type` descriptor.               |

The `elements`, `key`, and `value` fields each hold a nested `type` descriptor, so arrays of structs,
maps of structs, and nested arrays/maps are all supported. Native `Array`/`Map` types come from
`@LHType(isLHArray = true)` / `@LHType(isLHMap = true)` on task parameters/returns, or from array/`Map`
struct properties.

```yaml
tasks:
  sum-numbers:
    output:
      type:
        primitive: "INT"
    inputs:
    - name: "numbers"
      type:
        array:
          elements:
            type:
              primitive: "INT"
    config-name: "task.sum-numbers.name"
    description: "Sums a native Array of integers"
  count-items:
    output:
      type:
        primitive: "INT"
    inputs:
    - name: "items"
      type:
        map:
          key:
            type:
              primitive: "STR"
          value:
            type:
              primitive: "INT"
    config-name: "task.count-items.name"
    description: "Counts the entries in a native Map"
  create-order:
    output:
      type:
        struct: "order"
    inputs:
    - name: "address"
      type:
        struct: "shipping-address"
    config-name: "task.create-order.name"
    description: "Creates an order shipped to the given address"
structs:
  order:
    config-name: "struct.order.name"
    description: "A customer order"
    properties:
    - name: "productName"
      type:
        primitive: "STR"
    - name: "shippingAddress"
      type:
        struct: "shipping-address"
  shipping-address:
    config-name: "struct.shipping-address.name"
    description: "A shipping destination address"
    properties:
    - name: "street"
      type:
        primitive: "STR"
    - name: "zipCode"
      type:
        primitive: "INT"
```

# Building a Docker Image

Saddle bag Docker images are built and published with the
[`publish-saddle-bag`](https://github.com/littlehorse-enterprises/publish-saddle-bag) GitHub Action.
It builds the Quarkus application with Gradle, reads the OCI annotations from the generated
`properties` manifest, and publishes the resulting image to a container registry.

> **Note:** Only JVM-based Quarkus Docker images are supported. Native images are not supported yet.

See the [`publish-saddle-bag`](https://github.com/littlehorse-enterprises/publish-saddle-bag)
repository for full documentation, inputs, and usage examples.

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

``quarkus.littlehorse.saddle.bag.metadata.docker-image``
The Docker image that packages this saddle bag task worker.

* Type: string
* Importance: high

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
