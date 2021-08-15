package org.dexenjaeger.algebra.model.binaryoperator;

import org.dexenjaeger.algebra.utils.Builder;

import java.util.Map;

public abstract class BaseBinaryOperatorBuilder<T extends BinaryOperator> implements Builder<T> {
  protected String operatorSymbol = "*";
  protected int size = 0;
  protected String[] elements;
  protected Map<String, Integer> lookup;
  protected int[][] multiplicationTable;
  
  public BaseBinaryOperatorBuilder<T> size(int size) {
    this.size = size;
    return this;
  }
  
  public BaseBinaryOperatorBuilder<T> operatorSymbol(String operatorSymbol) {
    this.operatorSymbol = operatorSymbol;
    return this;
  }
  
  public BaseBinaryOperatorBuilder<T> elements(String... elements) {
    this.elements = elements;
    return this;
  }
  
  public BaseBinaryOperatorBuilder<T> lookup(Map<String, Integer> lookup) {
    this.lookup = lookup;
    return this;
  }
  
  public BaseBinaryOperatorBuilder<T> multiplicationTable(int[][] multiplicationTable) {
    this.multiplicationTable = multiplicationTable;
    return this;
  }
}
