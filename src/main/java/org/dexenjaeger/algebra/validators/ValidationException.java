package org.dexenjaeger.algebra.validators;

public class ValidationException extends Exception {
  public ValidationException(String message) {
    this(message, null);
  }
  
  public ValidationException(String message, Throwable cause) {
    super(message, cause);
  }
}
