// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
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
import io.vlingo.symbio.State.BinaryState;
import io.vlingo.symbio.State.TextState;

public class StateTest {

  @Test
  public void testEmptyBinaryState() {
    final BinaryState emptyState = new BinaryState();
    assertTrue(emptyState.isBinary());
    assertTrue(emptyState.isEmpty());
  }

  @Test
  public void testBasicBinaryState() {
    final String state = "test-state";
    final BinaryState basicState =
            new BinaryState("123", String.class, 1, state.getBytes(), 1);
    assertTrue(basicState.isBinary());
    assertFalse(basicState.isText());
    assertFalse(basicState.isEmpty());
    assertEquals("123", basicState.id);
    assertEquals(String.class.getName(), basicState.type);
    assertEquals(1, basicState.typeVersion);
    assertEquals(state, new String(basicState.data));
    assertFalse(basicState.hasMetadata());
  }

  @Test
  public void testBinaryStateWithMetadataOperation() {
    final String state = "test-state";
    final BinaryState metadataOperationState =
            new BinaryState("123", String.class, 1, state.getBytes(), 1, Metadata.withOperation("op"));
    assertTrue(metadataOperationState.isBinary());
    assertFalse(metadataOperationState.isText());
    assertFalse(metadataOperationState.isEmpty());
    assertEquals("123", metadataOperationState.id);
    assertEquals(String.class.getName(), metadataOperationState.type);
    assertEquals(state, new String(metadataOperationState.data));
    assertEquals(1, metadataOperationState.typeVersion);
    assertTrue(metadataOperationState.hasMetadata());
    assertTrue(metadataOperationState.metadata.hasOperation());
    assertEquals("op", metadataOperationState.metadata.operation);
    assertFalse(metadataOperationState.metadata.hasValue());
  }

  @Test
  public void testBinaryStateWithMetadataValue() {
    final String state = "test-state";
    final BinaryState metadataValueState =
            new BinaryState("123", String.class, 1, state.getBytes(), 1, Metadata.withValue("value"));
    assertTrue(metadataValueState.isBinary());
    assertFalse(metadataValueState.isText());
    assertFalse(metadataValueState.isEmpty());
    assertEquals("123", metadataValueState.id);
    assertEquals(String.class.getName(), metadataValueState.type);
    assertEquals(state, new String(metadataValueState.data));
    assertEquals(1, metadataValueState.typeVersion);
    assertTrue(metadataValueState.hasMetadata());
    assertTrue(metadataValueState.metadata.hasValue());
    assertEquals("value", metadataValueState.metadata.value);
    assertFalse(metadataValueState.metadata.hasOperation());
  }

  @Test
  public void testBinaryStateWithMetadata() {
    final String state = "test-state";
    final BinaryState metadataState =
            new BinaryState("123", String.class, 1, state.getBytes(), 1, Metadata.with("value", "op"));
    assertTrue(metadataState.isBinary());
    assertFalse(metadataState.isText());
    assertFalse(metadataState.isEmpty());
    assertEquals("123", metadataState.id);
    assertEquals(String.class.getName(), metadataState.type);
    assertEquals(state, new String(metadataState.data));
    assertEquals(1, metadataState.typeVersion);
    assertTrue(metadataState.hasMetadata());
    assertTrue(metadataState.metadata.hasValue());
    assertEquals("value", metadataState.metadata.value);
    assertTrue(metadataState.metadata.hasOperation());
    assertEquals("op", metadataState.metadata.operation);
  }

  @Test
  public void testEmptyTextState() {
    final TextState emptyState = new TextState();
    assertFalse(emptyState.isBinary());
    assertTrue(emptyState.isText());
    assertTrue(emptyState.isEmpty());
  }
}
