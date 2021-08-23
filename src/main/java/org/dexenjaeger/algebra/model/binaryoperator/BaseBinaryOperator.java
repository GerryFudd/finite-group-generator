package org.dexenjaeger.algebra.model.binaryoperator;

import lombok.Getter;
import org.dexenjaeger.algebra.utils.BinaryOperatorUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class BaseBinaryOperator implements BinaryOperator {
  @Getter
  protected final OperatorSymbol operatorSymbol;
  @Getter
  protected final int size;
  protected final Element[] elements;
  protected final Map<Element, Integer> lookup;
  protected final int[][] multiplicationTable;
  protected BaseBinaryOperator(
    OperatorSymbol operatorSymbol,
    int size,
    Element[] elements,
    Map<Element, Integer> lookup,
    int[][] multiplicationTable
  ) {
    this.operatorSymbol = operatorSymbol;
    this.size = size;
    this.elements = elements;
    this.lookup = lookup;
    this.multiplicationTable = multiplicationTable;
  }
  
  @Override
  public Set<Element> getElementsDisplay() {
    return Set.of(elements);
  }
  
  @Override
  public List<Element> getSortedElements() { return List.of(elements); }
  
  @Override
  public Element prod(Element a, Element b) {
    return elements[prod(lookup.get(a), lookup.get(b))];
  }
  
  @Override
  public int prod(int a, int b) {
    return multiplicationTable[a][b];
  }
  
  @Override
  public Integer eval(Element a) {
    return lookup.get(a);
  }
  
  @Override
  public Element display(int i) {
    return elements[i];
  }
  
  @Override
  public String toString() {
    return printMultiplicationTable();
  }
  
  @Override
  public String printMultiplicationTable() {
    return BinaryOperatorUtil.printMultiplicationTable(this);
  }
  
  @Override
  public int hashCode() {
    return 23 * lookup.hashCode() + Arrays.deepHashCode(multiplicationTable);
  }
  
  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null) {
      return false;
    }
    if (!(other instanceof BinaryOperator)) {
      return false;
    }
    if (size != ((BinaryOperator) other).getSize()) {
      return false;
    }
    
    for (int i = 0; i < getSize(); i++) {
      if (!display(i).equals(((BinaryOperator) other).display(i))) {
        return false;
      }
      for (int j = 0; j < getSize(); j++) {
        if (prod(i, j) != ((BinaryOperator) other).prod(i, j)) {
          return false;
        }
      }
    }
    return true;
  }
}
