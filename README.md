# vlingo-symbio

[![Build Status](https://travis-ci.org/vlingo/vlingo-symbio.svg?branch=master)](https://travis-ci.org/vlingo/vlingo-symbio) [ ![Download](https://api.bintray.com/packages/vlingo/vlingo-platform-java/vlingo-symbio/images/download.svg) ](https://bintray.com/vlingo/vlingo-platform-java/vlingo-symbio/_latestVersion)

The reactive, scalable, and resilient CQRS storage and projection tool for services and applications built on the vlingo/platform.

### Name
The name "symbio" highlights the symbiotic relationship between domain models and persistence mechanisms.
Domain models must be persisted and individual parts must be reconstituted to memory when needed. Persistence
mechanisms crave data to store. Hence we can conclude that the two need and benefit from the other.

Interestingly too is that the name "symbio" ends with the letters, i and o, for input and output.
The `StateStorage`, introduced next, produces domain model output to disk, and input from disk back to
the domain model.


### State Storage
The `StateStore` is a simple object storage mechanism that can be run against a number of persistence engines.
These are the available storage implementations:

   - In-memory binary: `InMemoryBinaryStateStoreActor`
   - In-memory text: `InMemoryTextStateStoreActor`
   - DynamoDB Text Store: `DynamoDBTextStateActor`
   - DynamoDB Binary Store: `DynamoDBBinaryStateActor`
   - General-purpose JDBC: `JDBCTextStateStoreActor`
   
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
      <version>0.3.6</version>
      <scope>compile</scope>
    </dependency>
  </dependencies>
```

```gradle
dependencies {
    compile 'io.vlingo:vlingo-symbio:0.3.6'
}

repositories {
    jcenter()
}
```

License (See LICENSE file for full license)
-------------------------------------------
Copyright © 2012-2018 Vaughn Vernon. All rights reserved.

This Source Code Form is subject to the terms of the
Mozilla Public License, v. 2.0. If a copy of the MPL
was not distributed with this file, You can obtain
one at https://mozilla.org/MPL/2.0/.
