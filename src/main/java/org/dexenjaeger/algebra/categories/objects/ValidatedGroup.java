package org.dexenjaeger.algebra.categories.objects;

import org.dexenjaeger.algebra.model.ValidatedGroupSpec;
import org.dexenjaeger.algebra.model.ValidatedMonoidSpec;
import org.dexenjaeger.algebra.categories.morphisms.ValidatingBinaryOperator;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class ValidatedGroup implements SafeGroup {
  private final Map<String, String> inversesMap;
  private final Map<Integer, Set<List<String>>> cyclesMap;
  private final Monoid monoid;
  
  private ValidatedGroup(
    Map<String, String> inversesMap,
    Map<Integer, Set<List<String>>> cyclesMap,
    Monoid monoid
    ) {
    this.inversesMap = inversesMap;
    this.cyclesMap = cyclesMap;
    this.monoid = monoid;
  }
  
  public static ValidatedGroup createGroup(ValidatedGroupSpec spec) {
    return createGroup(
      spec,
      (identity, binOp) -> ValidatedMonoid.createMonoid(new ValidatedMonoidSpec(spec.getOperatorSymbol(), identity, binOp))
    );
  }
  
  public static ValidatedGroup createGroup(
    ValidatedGroupSpec spec,
    BiFunction<String, ValidatingBinaryOperator, ValidatedMonoid> validatedMonoidConstructor
  ) {
    spec.getBinaryOperator().validateInverseMap(spec.getIdentity(), spec.getInversesMap());
    spec.getBinaryOperator().validateCyclesMap(spec.getCyclesMap(), spec.getIdentity(), spec.getInversesMap());
    
    return new ValidatedGroup(
      spec.getInversesMap(),
      spec.getCyclesMap(),
      validatedMonoidConstructor.apply(spec.getIdentity(), spec.getBinaryOperator())
    );
  }
  
  @Override
  public String getOperatorSymbol() {
    return monoid.getOperatorSymbol();
  }
  
  @Override
  public List<String> getElementsAsList() {
    return monoid.getElementsAsList();
  }
  
  @Override
  public String getProduct(String a, String b) {
    return monoid.getProduct(a, b);
  }
  
  @Override
  public String getIdentity() {
    return monoid.getIdentity();
  }
  
  @Override
  public String getInverse(String element) {
    return Optional.ofNullable(inversesMap.get(element))
             .orElseThrow(() -> new RuntimeException(""));
  }
  
  @Override
  public List<Integer> getCycleSizes() {
    return cyclesMap.keySet().stream().sorted().collect(Collectors.toList());
  }
  
  @Override
  public Set<List<String>> getNCycles(Integer n) {
    return cyclesMap.containsKey(n) ? cyclesMap.get(n) : Set.of();
  }
}
