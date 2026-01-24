# LittleHorse Quarkus RESTful Gateway Extension

A RESTful Gateway for [LittleHorse](https://littlehorse.io/).

# Table of Content

<!-- TOC -->
* [LittleHorse Quarkus RESTful Gateway Extension](#littlehorse-quarkus-restful-gateway-extension)
* [Table of Content](#table-of-content)
* [Installation](#installation)
* [OpenAPI](#openapi)
* [Configurations](#configurations)
  * [Passing Configurations](#passing-configurations)
  * [LittleHorse Client Configurations](#littlehorse-client-configurations)
* [Endpoints](#endpoints)
  * [ExternalEvent](#externalevent)
    * [Post](#post)
      * [Body parameter](#body-parameter)
      * [Parameters](#parameters)
      * [Example responses](#example-responses)
      * [Responses](#responses)
  * [TaskDef](#taskdef)
    * [Search](#search)
      * [Parameters](#parameters-1)
      * [Example responses](#example-responses-1)
      * [Responses](#responses-1)
    * [Get](#get)
      * [Parameters](#parameters-2)
      * [Example responses](#example-responses-2)
      * [Responses](#responses-2)
  * [WfRun](#wfrun)
    * [Run](#run)
      * [Body parameter](#body-parameter-1)
      * [Parameters](#parameters-3)
      * [Example responses](#example-responses-3)
      * [Responses](#responses-3)
    * [Get](#get-1)
      * [Parameters](#parameters-4)
      * [Example responses](#example-responses-4)
      * [Responses](#responses-4)
    * [Variables](#variables)
      * [Parameters](#parameters-5)
      * [Example responses](#example-responses-5)
      * [Responses](#responses-5)
  * [WfSpec](#wfspec)
    * [Search](#search-1)
      * [Parameters](#parameters-6)
      * [Example responses](#example-responses-6)
      * [Responses](#responses-6)
    * [Get Latest](#get-latest)
      * [Parameters](#parameters-7)
      * [Example responses](#example-responses-7)
      * [Responses](#responses-7)
    * [Get Version](#get-version)
      * [Parameters](#parameters-8)
      * [Example responses](#example-responses-8)
      * [Responses](#responses-8)
  * [Version](#version)
    * [Get](#get-2)
      * [Example responses](#example-responses-9)
      * [Responses](#responses-9)
* [Schemas](#schemas)
  * [ExternalEvent](#externalevent-1)
  * [ExternalEventRequest](#externaleventrequest)
    * [Properties](#properties)
  * [ServerInformationResponse](#serverinformationresponse)
    * [Properties](#properties-1)
  * [TaskDef](#taskdef-1)
  * [TaskDefIdList](#taskdefidlist)
  * [VariableList](#variablelist)
  * [WfRun](#wfrun-1)
  * [WfRunRequest](#wfrunrequest)
    * [Properties](#properties-2)
  * [WfSpec](#wfspec-1)
  * [WfSpecIdList](#wfspecidlist)
<!-- TOC -->

# Installation

<a href="https://central.sonatype.com/artifact/io.littlehorse/littlehorse-quarkus-restful-gateway"><img alt="Maven Central" src="https://img.shields.io/maven-central/v/io.littlehorse/littlehorse-quarkus-restful-gateway?label=latest"></a>

This extension is available at [Maven Central](https://central.sonatype.com/artifact/io.littlehorse/littlehorse-quarkus).

Gradle:

```groovy
implementation "io.littlehorse:littlehorse-quarkus-restful-gateway:${lhVersion}"
```

Maven:

```xml
<dependency>
    <groupId>io.littlehorse</groupId>
    <artifactId>littlehorse-quarkus-restful-gateway</artifactId>
    <version>${lhVersion}</version>
</dependency>
```

# OpenAPI

Enable [OpenAPI](https://quarkus.io/guides/openapi-swaggerui)
adding `io.quarkus:quarkus-smallrye-openapi` to your project's dependencies.
Check [SmallRye OpenAPI Extension](https://quarkus.io/extensions/io.quarkus/quarkus-smallrye-openapi/).

Gradle:

```groovy
implementation "io.quarkus:quarkus-smallrye-openapi"
```

Maven:

```xml
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-smallrye-openapi</artifactId>
</dependency>
```

Once your application is started, you can make a request to the default `/q/openapi` endpoint:

```shell
curl http://localhost:8080/q/openapi
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

# Endpoints

## ExternalEvent

### Post

```
POST /gateway/tenants/{tenant}/external-events
```

#### Body parameter

```json
{
  "externalEventDefName": "string",
  "wfRunId": "string",
  "guid": "string",
  "threadRunNumber": 0,
  "nodeRunPosition": 0,
  "content": null
}
```

#### Parameters

| Name   | In   | Type                                          | Required | Description |
|--------|------|-----------------------------------------------|----------|-------------|
| tenant | path | string                                        | true     | Tenant name |
| body   | body | [ExternalEventRequest](#externaleventrequest) | true     | none        |

#### Example responses

```
200 Response
```

```json
{
  "claimed": true,
  "content": {},
  "createdAt": "2026-01-24T17:45:15.268Z",
  "id": {
    "wfRunId": {
      "id": "fb7cfff0dda24f559af6daa8803c33f7"
    },
    "externalEventDefId": {
      "name": "my-external-event"
    },
    "guid": "f369dd9031c8435f9b31332bd3beaf96"
  },
  "nodeRunPosition": 1,
  "threadRunNumber": 0
}
```

#### Responses

| Status | Meaning                                                                    | Description                 | Schema                                                                |
|--------|----------------------------------------------------------------------------|-----------------------------|-----------------------------------------------------------------------|
| 200    | [OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)                    | Record created successfully | [ExternalEvent](https://littlehorse.io/docs/server/api#externalevent) |
| 400    | [Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)           | Bad request                 | None                                                                  |
| 404    | [Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)             | Record not found            | None                                                                  |
| 409    | [Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)              | Record already exists       | None                                                                  |
| 500    | [Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1) | Internal error              | None                                                                  |

## TaskDef

### Search

```
GET /gateway/tenants/{tenant}/task-defs
```

#### Parameters

| Name     | In    | Type           | Required | Description                                    |
|----------|-------|----------------|----------|------------------------------------------------|
| tenant   | path  | string         | true     | Tenant name                                    |
| bookmark | query | string         | false    | Bookmark for pagination, it is a base64 string |
| limit    | query | integer(int32) | false    | Maximum number of records to return            |
| prefix   | query | string         | false    | Search by prefix                               |

#### Example responses

```
200 Response
```

```json
{
  "results": [
    {
      "name": "greetings"
    }
  ],
  "bookmark": "Cg0IABIJEgcwL3ByaW50"
}
```

#### Responses

| Status | Meaning                                                                    | Description                 | Schema                                                                |
|--------|----------------------------------------------------------------------------|-----------------------------|-----------------------------------------------------------------------|
| 200    | [OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)                    | List retrieved successfully | [TaskDefIdList](https://littlehorse.io/docs/server/api#taskdefidlist) |
| 400    | [Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)           | Bad request                 | None                                                                  |
| 500    | [Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1) | Internal error              | None                                                                  |

### Get

```
GET /gateway/tenants/{tenant}/task-defs/{name}
```

#### Parameters

| Name   | In   | Type   | Required | Description |
|--------|------|--------|----------|-------------|
| name   | path | string | true     | none        |
| tenant | path | string | true     | Tenant name |

#### Example responses

```
200 Response
```

```json
{
  "id": {
    "name": "greetings"
  },
  "inputVars": [
    {
      "name": "name",
      "typeDef": {
        "primitiveType": "STR",
        "masked": false
      }
    }
  ],
  "createdAt": "2026-01-23T22:16:50.648Z",
  "returnType": {
    "returnType": {
      "primitiveType": "STR",
      "masked": false
    }
  }
}
```

#### Responses

| Status | Meaning                                                                    | Description                   | Schema                                                    |
|--------|----------------------------------------------------------------------------|-------------------------------|-----------------------------------------------------------|
| 200    | [OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)                    | Record retrieved successfully | [TaskDef](https://littlehorse.io/docs/server/api#taskdef) |
| 400    | [Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)           | Bad request                   | None                                                      |
| 404    | [Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)             | Record not found              | None                                                      |
| 500    | [Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1) | Internal error                | None                                                      |

## WfRun

### Run

```
POST /gateway/tenants/{tenant}/wf-runs
```

#### Body parameter

```json
{
  "wfSpecName": "string",
  "id": "string",
  "majorVersion": 0,
  "revision": 0,
  "parentWfRunId": "string",
  "variables": {
    "property1": null,
    "property2": null
  }
}
```

#### Parameters

| Name   | In   | Type                          | Required | Description |
|--------|------|-------------------------------|----------|-------------|
| tenant | path | string                        | true     | Tenant name |
| body   | body | [WfRunRequest](#wfrunrequest) | true     | none        |

#### Example responses

```
200 Response
```

```json
{
  "id": {
    "id": "597bfc2a10c346f6a01a04a84c3b6ee6"
  },
  "wfSpecId": {
    "name": "greetings",
    "majorVersion": 0,
    "revision": 0
  },
  "oldWfSpecVersions": [],
  "status": "RUNNING",
  "greatestThreadrunNumber": 0,
  "startTime": "2026-01-24T15:38:35.421Z",
  "threadRuns": [
    {
      "wfSpecId": {
        "name": "greetings",
        "majorVersion": 0,
        "revision": 0
      },
      "number": 0,
      "status": "RUNNING",
      "threadSpecName": "entrypoint",
      "startTime": "2026-01-24T15:38:35.424Z",
      "childThreadIds": [],
      "haltReasons": [],
      "currentNodePosition": 1,
      "handledFailedChildren": [],
      "type": "ENTRYPOINT"
    }
  ],
  "pendingInterrupts": [],
  "pendingFailures": []
}
```

#### Responses

| Status | Meaning                                                                    | Description                 | Schema                                                |
|--------|----------------------------------------------------------------------------|-----------------------------|-------------------------------------------------------|
| 200    | [OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)                    | Record created successfully | [WfRun](https://littlehorse.io/docs/server/api#wfrun) |
| 400    | [Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)           | Bad request                 | None                                                  |
| 404    | [Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)             | Record not found            | None                                                  |
| 409    | [Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)              | Record already exists       | None                                                  |
| 500    | [Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1) | Internal error              | None                                                  |

### Get

```
GET /gateway/tenants/{tenant}/wf-runs/{id}
```

#### Parameters

| Name   | In   | Type   | Required | Description |
|--------|------|--------|----------|-------------|
| id     | path | string | true     | none        |
| tenant | path | string | true     | Tenant name |

#### Example responses

```
200 Response
```

```json
{
  "id": {
    "id": "597bfc2a10c346f6a01a04a84c3b6ee6"
  },
  "wfSpecId": {
    "name": "greetings",
    "majorVersion": 0,
    "revision": 0
  },
  "oldWfSpecVersions": [],
  "status": "RUNNING",
  "greatestThreadrunNumber": 0,
  "startTime": "2026-01-24T15:38:35.421Z",
  "threadRuns": [
    {
      "wfSpecId": {
        "name": "greetings",
        "majorVersion": 0,
        "revision": 0
      },
      "number": 0,
      "status": "RUNNING",
      "threadSpecName": "entrypoint",
      "startTime": "2026-01-24T15:38:35.424Z",
      "childThreadIds": [],
      "haltReasons": [],
      "currentNodePosition": 1,
      "handledFailedChildren": [],
      "type": "ENTRYPOINT"
    }
  ],
  "pendingInterrupts": [],
  "pendingFailures": []
}
```

#### Responses

| Status | Meaning                                                                    | Description                   | Schema                                                |
|--------|----------------------------------------------------------------------------|-------------------------------|-------------------------------------------------------|
| 200    | [OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)                    | Record retrieved successfully | [WfRun](https://littlehorse.io/docs/server/api#wfrun) |
| 400    | [Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)           | Bad request                   | None                                                  |
| 404    | [Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)             | Record not found              | None                                                  |
| 500    | [Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1) | Internal error                | None                                                  |

### Variables

```
GET /gateway/tenants/{tenant}/wf-runs/{id}/variables
```

#### Parameters

| Name   | In   | Type   | Required | Description |
|--------|------|--------|----------|-------------|
| id     | path | string | true     | none        |
| tenant | path | string | true     | Tenant name |

#### Example responses

```
200 Response
```

```json
{
  "results": [
    {
      "id": {
        "wfRunId": {
          "id": "my-id"
        },
        "threadRunNumber": 0,
        "name": "name"
      },
      "value": {
        "str": "Anakin"
      },
      "createdAt": "2026-01-24T16:17:43.337Z",
      "wfSpecId": {
        "name": "greetings",
        "majorVersion": 0,
        "revision": 0
      },
      "masked": false
    }
  ]
}
```

#### Responses

| Status | Meaning                                                                    | Description                 | Schema                                                              |
|--------|----------------------------------------------------------------------------|-----------------------------|---------------------------------------------------------------------|
| 200    | [OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)                    | List retrieved successfully | [VariableList](https://littlehorse.io/docs/server/api#variablelist) |
| 400    | [Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)           | Bad request                 | None                                                                |
| 404    | [Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)             | Record not found            | None                                                                |
| 500    | [Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1) | Internal error              | None                                                                |

## WfSpec

### Search

```
GET /gateway/tenants/{tenant}/wf-specs
```

#### Parameters

| Name     | In    | Type           | Required | Description                                    |
|----------|-------|----------------|----------|------------------------------------------------|
| tenant   | path  | string         | true     | Tenant name                                    |
| bookmark | query | string         | false    | Bookmark for pagination, it is a base64 string |
| limit    | query | integer(int32) | false    | Maximum number of records to return            |
| prefix   | query | string         | false    | Search by prefix                               |

#### Example responses

```
200 Response
```

```json
{
  "results": [
    {
      "name": "greetings",
      "majorVersion": 0,
      "revision": 0
    }
  ],
  "bookmark": "ChgIABIUEhIyL3dmLTAvMDAwMDAvMDAwMDA="
}
```

#### Responses

| Status | Meaning                                                                    | Description                 | Schema                                                              |
|--------|----------------------------------------------------------------------------|-----------------------------|---------------------------------------------------------------------|
| 200    | [OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)                    | List retrieved successfully | [WfSpecIdList](https://littlehorse.io/docs/server/api#wfspecidlist) |
| 400    | [Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)           | Bad request                 | None                                                                |
| 500    | [Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1) | Internal error              | None                                                                |

### Get Latest

```
GET /gateway/tenants/{tenant}/wf-specs/{name}
```

#### Parameters

| Name    | In   | Type   | Required | Description |
|---------|------|--------|----------|-------------|
| name    | path | string | true     | none        |
| tenant  | path | string | true     | Tenant name |

#### Example responses

```
200 Response
```

```json
{
  "id": {
    "name": "greetings",
    "majorVersion": 0,
    "revision": 0
  },
  "createdAt": "2026-01-20T16:10:06.917Z",
  "frozenVariables": [],
  "status": "ACTIVE",
  "threadSpecs": {
    "entrypoint": {
      "nodes": {
        "0-entrypoint-ENTRYPOINT": {
          "outgoingEdges": [
            {
              "sinkNodeName": "1-greetings-TASK",
              "variableMutations": []
            }
          ],
          "failureHandlers": [],
          "entrypoint": {}
        },
        "1-greetings-TASK": {
          "outgoingEdges": [
            {
              "sinkNodeName": "2-exit-EXIT",
              "variableMutations": []
            }
          ],
          "failureHandlers": [],
          "task": {
            "taskDefId": {
              "name": "greetings"
            },
            "timeoutSeconds": 60,
            "retries": 0,
            "variables": [
              {
                "variableName": "name"
              }
            ]
          }
        },
        "2-exit-EXIT": {
          "outgoingEdges": [],
          "failureHandlers": [],
          "exit": {}
        }
      },
      "variableDefs": [
        {
          "varDef": {
            "name": "name",
            "typeDef": {
              "primitiveType": "STR",
              "masked": false
            }
          },
          "required": false,
          "searchable": false,
          "jsonIndexes": [],
          "accessLevel": "PRIVATE_VAR"
        }
      ],
      "interruptDefs": []
    }
  },
  "entrypointThreadName": "entrypoint"
}
```

#### Responses

| Status | Meaning                                                                    | Description                   | Schema                                                  |
|--------|----------------------------------------------------------------------------|-------------------------------|---------------------------------------------------------|
| 200    | [OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)                    | Record retrieved successfully | [WfSpec](https://littlehorse.io/docs/server/api#wfspec) |
| 400    | [Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)           | Bad request                   | None                                                    |
| 404    | [Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)             | Record not found              | None                                                    |
| 500    | [Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1) | Internal error                | None                                                    |

### Get Version

```
GET /gateway/tenants/{tenant}/wf-specs/{name}/versions/{version}
```

#### Parameters

| Name    | In   | Type   | Required | Description                                     |
|---------|------|--------|----------|-------------------------------------------------|
| name    | path | string | true     | none                                            |
| tenant  | path | string | true     | Tenant name                                     |
| version | path | string | true     | Format: `MajorVersion.Revision`, example: `1.0` |

#### Example responses

```
200 Response
```

```json
{
  "id": {
    "name": "greetings",
    "majorVersion": 0,
    "revision": 0
  },
  "createdAt": "2026-01-20T16:10:06.917Z",
  "frozenVariables": [],
  "status": "ACTIVE",
  "threadSpecs": {
    "entrypoint": {
      "nodes": {
        "0-entrypoint-ENTRYPOINT": {
          "outgoingEdges": [
            {
              "sinkNodeName": "1-greetings-TASK",
              "variableMutations": []
            }
          ],
          "failureHandlers": [],
          "entrypoint": {}
        },
        "1-greetings-TASK": {
          "outgoingEdges": [
            {
              "sinkNodeName": "2-exit-EXIT",
              "variableMutations": []
            }
          ],
          "failureHandlers": [],
          "task": {
            "taskDefId": {
              "name": "greetings"
            },
            "timeoutSeconds": 60,
            "retries": 0,
            "variables": [
              {
                "variableName": "name"
              }
            ]
          }
        },
        "2-exit-EXIT": {
          "outgoingEdges": [],
          "failureHandlers": [],
          "exit": {}
        }
      },
      "variableDefs": [
        {
          "varDef": {
            "name": "name",
            "typeDef": {
              "primitiveType": "STR",
              "masked": false
            }
          },
          "required": false,
          "searchable": false,
          "jsonIndexes": [],
          "accessLevel": "PRIVATE_VAR"
        }
      ],
      "interruptDefs": []
    }
  },
  "entrypointThreadName": "entrypoint"
}
```

#### Responses

| Status | Meaning                                                                    | Description                   | Schema                                                  |
|--------|----------------------------------------------------------------------------|-------------------------------|---------------------------------------------------------|
| 200    | [OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)                    | Record retrieved successfully | [WfSpec](https://littlehorse.io/docs/server/api#wfspec) |
| 400    | [Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)           | Bad request                   | None                                                    |
| 404    | [Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)             | Record not found              | None                                                    |
| 500    | [Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1) | Internal error                | None                                                    |

## Version

### Get

`GET /gateway/version`

#### Example responses

```
200 Response
```

```json
{
  "version": "string"
}
```

#### Responses

| Status | Meaning                                                                    | Description                   | Schema                                                  |
|--------|----------------------------------------------------------------------------|-------------------------------|---------------------------------------------------------|
| 200    | [OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)                    | Record retrieved successfully | [ServerInformationResponse](#serverinformationresponse) |
| 500    | [Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1) | Internal error                | None                                                    |

# Schemas

## ExternalEvent

External documentation: [ExternalEvent](https://littlehorse.io/docs/server/api#externalevent).

## ExternalEventRequest

```json
{
  "externalEventDefName": "string",
  "wfRunId": "string",
  "guid": "string",
  "threadRunNumber": 0,
  "nodeRunPosition": 0,
  "content": null
}

```

### Properties

| Name                 | Type           | Required | Restrictions | Description |
|----------------------|----------------|----------|--------------|-------------|
| externalEventDefName | string         | true     | none         | none        |
| wfRunId              | string         | true     | none         | none        |
| guid                 | string         | false    | none         | none        |
| threadRunNumber      | integer(int32) | false    | none         | none        |
| nodeRunPosition      | integer(int32) | false    | none         | none        |
| content              | any            | false    | none         | none        |

## ServerInformationResponse

```json
{
  "version": "string"
}

```

### Properties

| Name    | Type   | Required | Restrictions | Description |
|---------|--------|----------|--------------|-------------|
| version | string | false    | none         | none        |

## TaskDef

External documentation: [TaskDef](https://littlehorse.io/docs/server/api#taskdef).

## TaskDefIdList

External documentation: [TaskDefIdList](https://littlehorse.io/docs/server/api#taskdefidlist).

## VariableList

External documentation: [VariableList](https://littlehorse.io/docs/server/api#variablelist).

## WfRun

External documentation: [WfRun](https://littlehorse.io/docs/server/api#wfrun).

## WfRunRequest

```json
{
  "wfSpecName": "string",
  "id": "string",
  "majorVersion": 0,
  "revision": 0,
  "parentWfRunId": "string",
  "variables": {
    "property1": null,
    "property2": null
  }
}

```

### Properties

| Name                       | Type           | Required | Restrictions | Description |
|----------------------------|----------------|----------|--------------|-------------|
| wfSpecName                 | string         | true     | none         | none        |
| id                         | string         | false    | none         | none        |
| majorVersion               | integer(int32) | false    | none         | none        |
| revision                   | integer(int32) | false    | none         | none        |
| parentWfRunId              | string         | false    | none         | none        |
| variables                  | object         | false    | none         | none        |
| » **additionalProperties** | any            | false    | none         | none        |

## WfSpec

External documentation: [WfSpec](https://littlehorse.io/docs/server/api#wfspec).

## WfSpecIdList

External documentation: [WfSpecIdList](https://littlehorse.io/docs/server/api#wfspecidlist).
