// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.symbio;

public class EntryBundle {
  public Entry<?> entry;
  public Source<?> source;

  public EntryBundle(final Entry<?> entry, final Source<?> source) {
    this.entry = entry;
    this.source = source;
  }

  public EntryBundle(final Entry<?> entry) {
    this.entry = entry;
    this.source = null;
  }

  @SuppressWarnings("unchecked")
  public <E extends Entry<?>> E typedEntry() {
    return (E) entry;
  }

  @SuppressWarnings("unchecked")
  public <S extends Source<?>> S typedSource() {
    return (S) source;
  }
}
