package io.vlingo.symbio.store;

public final class StorageException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public final Result result;

  public StorageException(final Result result, String message, Throwable cause) {
    super(message, cause);

    this.result = result;
  }

  public StorageException(final Result result, String message) {
    super(message);

    this.result = result;
  }

  @Override
  public boolean equals(final Object other) {
    if (other == null || !other.getClass().equals(getClass())) {
      return false;
    }
    return this.result == ((StorageException) other).result;
  }

  @Override
  public int hashCode() {
    return 31 * result.hashCode();
  }
}
