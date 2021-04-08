// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.symbio.store.object.inmemory;

import java.util.concurrent.atomic.AtomicLong;

import io.vlingo.xoom.symbio.store.object.StateObject;

public class Person extends StateObject implements Comparable<Person> {
  private static final long serialVersionUID = 1L;

  private static final AtomicLong identityGenerator = new AtomicLong(0);

  public final int age;
  public final String name;

  public Person(final String name, final int age) {
    super(identityGenerator.incrementAndGet());

    this.name = name;
    this.age = age;
  }

  @Override
  public int hashCode() {
    return 31 * name.hashCode() * age;
  }

  @Override
  public boolean equals(final Object other) {
    if (other == null || other.getClass() != getClass()) {
      return false;
    } else if (this == other) {
      return true;
    }

    final Person otherPerson = (Person) other;

    return this.persistenceId() == otherPerson.persistenceId();
  }

  @Override
  public String toString() {
    return "Person[persistenceId=" + persistenceId() + " name=" + name + " age=" + age + "]";
  }

  @Override
  public int compareTo(final Person otherPerson) {
    return Long.compare(this.persistenceId(), otherPerson.persistenceId());
  }
}
