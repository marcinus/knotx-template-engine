package io.knotx.te.core.exception;

/**
 * Thrown when template engine that is not registered is called.
 */
public class UnsupportedEngineException extends RuntimeException {

  public UnsupportedEngineException(String message) {
    super(message);
  }
}
