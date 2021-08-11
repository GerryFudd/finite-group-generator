package org.dexenjaeger.algebra.model.binaryoperator;

public class BinaryOperatorBuilder extends BaseBinaryOperatorBuilder<BinaryOperator> {
  @Override
  public BinaryOperator build() {
    return new ConcreteBinaryOperator(
      operatorSymbol, resolveSize(), resolveElements(),
      resolveLookup(), resolveMultiplicationTable()
    );
  }
}
