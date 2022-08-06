// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.symbio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MetadataTest {

  @Test
  public void testMetadataEmpty() {
    final Metadata metadata = new Metadata();
    assertFalse(metadata.hasValue());
    assertFalse(metadata.hasOperation());
    assertFalse(metadata.hasProperties());
  }

  @Test
  public void testNullMetadata() {
    final Metadata metadata = Metadata.nullMetadata();
    assertFalse(metadata.hasValue());
    assertFalse(metadata.hasOperation());
    assertFalse(metadata.hasProperties());
  }

  @Test
  @Deprecated
  public void testMetadataObject() {
    final Object object = new Object();
    final Metadata metadata = Metadata.withObject(object);
    assertTrue(metadata.hasObject());
    assertEquals(object, metadata.object);
    assertFalse(metadata.hasValue());
    assertFalse(metadata.hasOperation());
  }

  @Test
  public void testMetadataProperties() {
    final Map<String, String> properties = new HashMap<String, String>() {{
      put("prop1", "value1");
      put("prop2", "value2");
    }};
    final Metadata metadata = Metadata.withProperties(properties);
    assertTrue(metadata.hasProperties());
    assertEquals(properties, metadata.properties);
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

  @Test
  public void testMetadataPropertiesValueOperation() {
    final Map<String, String> properties = new HashMap<String, String>() {{
      put("prop1", "value1");
      put("prop2", "value2");
    }};
    final Metadata metadata = Metadata.with(properties, "value", "op");
    assertTrue(metadata.hasProperties());
    assertEquals(properties, metadata.properties);
    assertTrue(metadata.hasValue());
    assertEquals("value", metadata.value);
    assertTrue(metadata.hasOperation());
    assertEquals("op", metadata.operation);
  }

  @Test
  public void testMetadataWithClassOperationType() {
    final Map<String, String> properties = Collections.singletonMap("prop1", "value1");
    final Metadata metadata = Metadata.with(properties, "value", MetadataTest.class);
    assertTrue(metadata.hasProperties());
    assertEquals(properties, metadata.properties);
    assertTrue(metadata.hasValue());
    assertEquals("value", metadata.value);
    assertTrue(metadata.hasOperation());
    assertEquals("MetadataTest", metadata.operation);
  }

  @Test
  public void testMetadataWithNonCompactClassOperationType() {
    final Map<String, String> properties = Collections.singletonMap("prop1", "value1");
    final Metadata metadata = Metadata.with(properties, "value", MetadataTest.class, false);
    assertTrue(metadata.hasProperties());
    assertEquals(properties, metadata.properties);
    assertTrue(metadata.hasValue());
    assertEquals("value", metadata.value);
    assertTrue(metadata.hasOperation());
    assertEquals("io.vlingo.xoom.symbio.MetadataTest", metadata.operation);
  }
}
