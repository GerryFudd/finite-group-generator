package org.dexenjaeger.algebra.validators;

public interface Validator<T> {
  void validate(T item) throws ValidationException;
}
