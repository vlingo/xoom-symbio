package io.vlingo.xoom.symbio.store;

/**
 * The purpose of the query.
 */
public enum QueryMode {
  ReadOnly {
    @Override public boolean isReadOnly() { return true; }
  },
  ReadUpdate {
    @Override public boolean isReadUpdate() { return true; }
  };

  public boolean isReadOnly() {
    return false;
  }

  public boolean isReadUpdate() {
    return false;
  }
}
