package org.dexenjaeger.algebra.categories.objects.group;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.dexenjaeger.algebra.model.binaryoperator.BinaryOperator;
import org.dexenjaeger.algebra.model.binaryoperator.Element;
import org.dexenjaeger.algebra.model.binaryoperator.OperatorSymbol;
import org.dexenjaeger.algebra.model.cycle.IntCycle;
import org.dexenjaeger.algebra.utils.BinaryOperatorUtil;
import org.dexenjaeger.algebra.utils.CycleUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
public class TrivialGroup implements Group {
  private final CycleUtils cycleUtils = new CycleUtils();
  @Getter
  private final OperatorSymbol operatorSymbol;
  @Getter
  private final Element identityDisplay;
  
  public TrivialGroup() {
    this(Element.I);
  }
  public TrivialGroup(Element identityDisplay) {
    this(OperatorSymbol.DEFAULT, identityDisplay);
  }
  
  @Override
  public Element getInverse(Element element) {
    return identityDisplay;
  }
  
  @Override
  public int getInverse(int element) {
    return 0;
  }
  
  @Override
  public List<Integer> getCycleSizes() {
    return List.of(1);
  }
  
  @Override
  public Set<IntCycle> getNCycles(int n) {
    return n == 1 ?
             cycleUtils.createSingleIntCycle(0) :
             Set.of();
  }
  
  @Override
  public Set<Integer> getNCycleGenerators(int n) {
    return n == 1 ? Set.of(0) : Set.of();
  }
  
  @Override
  public Set<IntCycle> getMaximalCycles() {
    return cycleUtils.createSingleIntCycle(0);
  }
  
  @Override
  public Optional<IntCycle> getCycleGeneratedBy(int x) {
    return x != 0 ? Optional.empty() : Optional.of(cycleUtils.createIntCycle(0));
  }
  
  @Override
  public int[][] getMultiplicationTable() {
    return new int[][]{{0}};
  }
  
  @Override
  public Set<Element> getElementsDisplay() {
    return Set.of(identityDisplay);
  }
  
  @Override
  public int getSize() {
    return 1;
  }
  
  @Override
  public Element prod(Element a, Element b) {
    return identityDisplay;
  }
  
  @Override
  public int prod(int a, int b) {
    return 0;
  }
  
  @Override
  public Integer eval(Element a) {
    return 0;
  }
  
  @Override
  public Element display(int i) {
    return identityDisplay;
  }
  
  @Override
  public String printMultiplicationTable() {
    return BinaryOperatorUtil.printMultiplicationTable(this);
  }
  
  @Override
  public List<Element> getSortedElements() {
    return List.of(identityDisplay);
  }
  
  @Override
  public int getIdentity() {
    return 0;
  }
  
  @Override
  public int hashCode() {
    return 23 * Map.of(identityDisplay, 0).hashCode() + Arrays.deepHashCode(new int[][] {new int[]{0}});
  }
  
  @Override
  public boolean equals(Object other) {
    return (other instanceof BinaryOperator)
      && ((BinaryOperator) other).getSize() == 1
      && ((BinaryOperator) other).display(0).equals(identityDisplay);
  }
  
  @Override
  public String toString() {
    return printMultiplicationTable();
  }
}
