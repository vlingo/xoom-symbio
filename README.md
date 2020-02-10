# vlingo-symbio

[![Javadocs](http://javadoc.io/badge/io.vlingo/vlingo-symbio.svg?color=brightgreen)](http://javadoc.io/doc/io.vlingo/vlingo-symbio) [![Build Status](https://travis-ci.org/vlingo/vlingo-symbio.svg?branch=master)](https://travis-ci.org/vlingo/vlingo-symbio) [ ![Download](https://api.bintray.com/packages/vlingo/vlingo-platform-java/vlingo-symbio/images/download.svg) ](https://bintray.com/vlingo/vlingo-platform-java/vlingo-symbio/_latestVersion) [![Gitter chat](https://badges.gitter.im/gitterHQ/gitter.png)](https://gitter.im/vlingo-platform-java/symbio)

The vlingo/PLATFORM reactive, scalable, and resilient Object, CQRS, and Event Sourcing storage tools for services and applications.

### Name
The name "symbio" highlights the symbiotic relationship between domain models and persistence mechanisms.
Domain models must be persisted and individual parts must be reconstituted to memory when needed. Persistence
mechanisms crave data to store. Hence we can conclude that the two need and benefit from the other.

Interestingly too is that the name "symbio" ends with the letters, i and o, for input and output.
The `StateStorage`, introduced next, produces domain model output to disk, and input from disk back to
the domain model.

### Journal Storage
The `Journal` and related protocols support simple-to-use Event Sourcing, including `JournalReader` for
streaming across all entries in the journal, and `StreamReader` for reaching individual "sub-streams"
belonging to entities/aggregates in your application. There is a growing number of implementations:

   - JDBC over Postgres: `PostgresJournalActor` and supporting asynchronous readers
   - FoundationDB support: `FoundationDBJournalActor` and supporting asynchronous readers

### Object Storage
The `ObjectStore` is a simple object-relational mapped storage mechanism that can be run against a number of
persistence engines. These are the available implementations:

   - Jdbi over JDBC: `JdbiObjectStoreDelegate` controlled under `JDBCObjectStoreActor`
   - JPA standard: `JPAObjectStoreDelegate` for JPA implementations, including EclipseLink, OpenJPA, and Hibernate

### State Storage
The `StateStore` is a simple CQRS Key-CLOB/BLOB storage mechanism that can be run against a number of persistence engines.
Use it for both Command/Write Models and Query/Read Models. These are the available storage implementations:

   - In-memory binary: `InMemoryBinaryStateStoreActor`
   - In-memory text: `InMemoryTextStateStoreActor`
   - DynamoDB Text Store: `DynamoDBTextStateActor`
   - DynamoDB Binary Store: `DynamoDBBinaryStateActor`
   - General-purpose JDBC: `JDBCTextStateStoreActor`
   - Apache Geode: `GeodeStateStoreActor`
   
The `JDBCTextStateStoreActor` has these database delegate implementations:

   - HSQLDB: `HSQLDBStorageDelegate`
   - PostgresSQL: `PostgresStorageDelegate`

Adding additional JDBC storage delegates is a straightforward process requiring a few hours of work.

We welcome you to add support for your favorite database!

### Bintray

```xml
  <repositories>
    <repository>
      <id>jcenter</id>
      <url>https://jcenter.bintray.com/</url>
    </repository>
  </repositories>
  <dependencies>
    <dependency>
      <groupId>io.vlingo</groupId>
      <artifactId>vlingo-symbio</artifactId>
      <version>1.2.0</version>
      <scope>compile</scope>
    </dependency>
  </dependencies>
```

```gradle
dependencies {
    compile 'io.vlingo:vlingo-symbio:1.2.0'
}

repositories {
    jcenter()
}
```

License (See LICENSE file for full license)
-------------------------------------------
Copyright © 2012-2020 VLINGO LABS. All rights reserved.

This Source Code Form is subject to the terms of the
Mozilla Public License, v. 2.0. If a copy of the MPL
was not distributed with this file, You can obtain
one at https://mozilla.org/MPL/2.0/.
