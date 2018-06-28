// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import io.vlingo.symbio.Metadata;

public class MetadataTest {

  @Test
  public void testMetadataEmpty() {
    final Metadata metadata = new Metadata();
    assertFalse(metadata.hasValue());
    assertFalse(metadata.hasOperation());
  }

  @Test
  public void testMetadataValue() {
    final Metadata metadata = Metadata.withValue("value");
    assertTrue(metadata.hasValue());
    assertEquals("value", metadata.value);
    assertFalse(metadata.hasOperation());
  }

  @Test
  public void testMetadataOperation() {
    final Metadata metadata = Metadata.withOperation("op");
    assertFalse(metadata.hasValue());
    assertTrue(metadata.hasOperation());
    assertEquals("op", metadata.operation);
  }

  @Test
  public void testMetadataValueOperation() {
    final Metadata metadata = Metadata.with("value", "op");
    assertTrue(metadata.hasValue());
    assertEquals("value", metadata.value);
    assertTrue(metadata.hasOperation());
    assertEquals("op", metadata.operation);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMetadataIllegalValue() {
    Metadata.with(null, "op");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMetadataIllegalOperation() {
    Metadata.with("value", null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMetadataIllegalValueOperation() {
    // this actually fails on value test as already tested above
    Metadata.with(null, null);
  }
}
