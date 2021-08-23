package org.dexenjaeger.algebra.model.binaryoperator;

import org.dexenjaeger.algebra.utils.Builder;

import java.util.Map;

public abstract class BaseBinaryOperatorBuilder<T extends BinaryOperator> implements Builder<T> {
  protected OperatorSymbol operatorSymbol = OperatorSymbol.DEFAULT;
  protected int size = 0;
  protected Element[] elements;
  protected Map<Element, Integer> lookup;
  protected int[][] multiplicationTable;
  
  public BaseBinaryOperatorBuilder<T> size(int size) {
    this.size = size;
    return this;
  }
  
  public BaseBinaryOperatorBuilder<T> operatorSymbol(OperatorSymbol operatorSymbol) {
    this.operatorSymbol = operatorSymbol;
    return this;
  }
  
  public BaseBinaryOperatorBuilder<T> elements(Element... elements) {
    this.elements = elements;
    return this;
  }
  
  public BaseBinaryOperatorBuilder<T> lookup(Map<Element, Integer> lookup) {
    this.lookup = lookup;
    return this;
  }
  
  public BaseBinaryOperatorBuilder<T> multiplicationTable(int[][] multiplicationTable) {
    this.multiplicationTable = multiplicationTable;
    return this;
  }
}
