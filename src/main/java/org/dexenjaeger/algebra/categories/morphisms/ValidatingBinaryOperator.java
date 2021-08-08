package org.dexenjaeger.algebra.categories.morphisms;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

@RequiredArgsConstructor
public class ValidatingBinaryOperator {
  @Getter
  private final String[] elements;
  private final Map<String, Integer> reverseLookup;
  private final BiFunction<Integer, Integer, Integer> binaryOperator;
  
  private Integer lookup(String element) {
    return Optional.ofNullable(reverseLookup.get(element))
      .orElseThrow(() -> new RuntimeException(String.format(
        "Element \"%s\" doesn't exist in %s",
        element, String.join(", ", elements)
      )));
  }
  
  public String prod(String a, String b) {
    return elements[binaryOperator.apply(lookup(a),lookup(b))];
  }
  
  @SuppressWarnings("BooleanMethodIsAlwaysInverted")
  public boolean isValid() {
    if (elements.length != reverseLookup.entrySet().size()) {
      return false;
    }
    
    for (int i = 0; i < elements.length; i++) {
      if (i != reverseLookup.get(elements[i])) {
        return false;
      }
      for (int j = 0; j < elements.length; j++) {
        int product = binaryOperator.apply(i, j);
        if (product < 0 || elements.length <= product) {
          return false;
        }
      }
    }
    
    return true;
  }
  
  @SuppressWarnings("BooleanMethodIsAlwaysInverted")
  public boolean isAssociative() {
    for (int i = 0; i < elements.length; i++) {
      for (int j = 0; j < elements.length; j++) {
        for (int k = 0; k < elements.length; k++) {
          if (
            !binaryOperator.apply(binaryOperator.apply(i, j), k).equals(binaryOperator.apply(i, binaryOperator.apply(j, k)))
          ) {
            return false;
          }
        }
      }
    }
    return true;
  }
  
  @SuppressWarnings("BooleanMethodIsAlwaysInverted")
  public boolean isIdentity(String id) {
    for (String element:elements) {
      if (!prod(id, element).equals(element) || !prod(element, id).equals(element)) {
        return false;
      }
    }
    return true;
  }
  
  private RuntimeException getInerseMapNotValidException(Exception e) {
    return new RuntimeException("Inverse map is not valid.", e);
  }
  
  @SuppressWarnings("BooleanMethodIsAlwaysInverted")
  public void validateInverseMap(String identity, Map<String, String> inversesMap) {
    for (String element:elements) {
      if (!inversesMap.containsKey(element)) {
        throw getInerseMapNotValidException(null);
      }
      String inverse;
      try {
        inverse = elements[lookup(inversesMap.get(element))];
      } catch (RuntimeException e) {
        throw getInerseMapNotValidException(e);
      }
      
      if (
        !identity.equals(prod(element, inverse))
          || !identity.equals(prod(inverse, element))
      ) {
        throw getInerseMapNotValidException(null);
      }
    }
  }
  
  private RuntimeException getCyclesMapException(String reason) {
    return new RuntimeException(String.format("Invalid cycles map: %s.", reason));
  }
  
  public void validateCyclesMap(Map<Integer, Set<List<String>>> cyclesMap, String identity, Map<String, String> inversesMap) {
    if (cyclesMap == null || cyclesMap.isEmpty()) {
      throw getCyclesMapException("map is null");
    }
    for (Integer n:cyclesMap.keySet()) {
      for (List<String> cycle:cyclesMap.get(n)) {
        LinkedList<String> linkedCycle = new LinkedList<>(cycle);
        if (linkedCycle.isEmpty()) {
          throw getCyclesMapException("there is an empty cycle");
        }
        if (!linkedCycle.removeLast().equals(identity)) {
          throw getCyclesMapException("cycle doesn't end with identity");
        }
        String generator = null;
        String previous = null;
        while (!linkedCycle.isEmpty()) {
          String a = linkedCycle.removeFirst();
          if (generator == null) {
            generator = a;
            previous = a;
          } else if (!a.equals(prod(generator, previous))) {
            throw getCyclesMapException("cycle is improperly generated");
          } else {
            previous = a;
          }
          if (a.equals(identity)) {
            throw getCyclesMapException("cycle contains identity in middle");
          }
          if (linkedCycle.isEmpty()) {
            if (!a.equals(inversesMap.get(a))) {
              throw getCyclesMapException("cycle doesn't contain the inverse of each element");
            }
          } else if (!linkedCycle.removeLast().equals(inversesMap.get(a))) {
            throw getCyclesMapException("cycle doesn't contain the inverse of each element");
          }
        }
      }
    }
  }
}
