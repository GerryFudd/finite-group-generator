package org.dexenjaeger.algebra.model.binaryoperator;

import org.dexenjaeger.algebra.utils.BinaryOperatorUtil;
import org.dexenjaeger.algebra.utils.Builder;

import java.util.Map;
import java.util.function.BiFunction;

public abstract class BaseBinaryOperatorBuilder<T extends BinaryOperator> implements Builder<T> {
  protected String operatorSymbol = "*";
  protected int size = 0;
  protected String[] elements;
  protected Map<String, Integer> lookup;
  protected BiFunction<Integer, Integer, Integer> operator;
  protected int[][] multiplicationTable;
  
  protected int resolveSize() {
    if (size > 0) {
      return size;
    }
    if (elements != null) {
      return elements.length;
    }
    if (lookup != null) {
      return lookup.size();
    }
    throw new RuntimeException("Size is not defined.");
  }
  
  protected String[] resolveElements() {
    if (elements != null) {
      return elements;
    }
    if (lookup != null) {
      return BinaryOperatorUtil.createElementsList(lookup);
    }
    
    throw new RuntimeException("Elements are not defined.");
  }
  
  protected Map<String, Integer> resolveLookup() {
    if (lookup != null) {
      return lookup;
    }
    if (elements != null) {
      return BinaryOperatorUtil.createLookup(elements);
    }
    throw new RuntimeException("Lookup is not defined.");
  }
  
  protected int[][] resolveMultiplicationTable() {
    if (multiplicationTable != null) {
      return multiplicationTable;
    }
    return BinaryOperatorUtil.getMultiplicationTable(
      resolveSize(),
      operator
    );
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
  
  public BaseBinaryOperatorBuilder<T> operator(BiFunction<Integer, Integer, Integer> operator) {
    this.operator = operator;
    return this;
  }
  
  public BaseBinaryOperatorBuilder<T> multiplicationTable(int[][] multiplicationTable) {
    this.multiplicationTable = multiplicationTable;
    return this;
  }
}
