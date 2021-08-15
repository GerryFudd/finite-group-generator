package org.dexenjaeger.algebra.model.binaryoperator;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class BinaryOperatorBuilder extends BaseBinaryOperatorBuilder<BinaryOperator> {
  @Override
  public BinaryOperator build() {
    return new ConcreteBinaryOperator(
      operatorSymbol, size, elements,
      lookup, multiplicationTable
    );
  }
}
