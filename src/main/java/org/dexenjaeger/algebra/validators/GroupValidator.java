package org.dexenjaeger.algebra.validators;

import org.dexenjaeger.algebra.categories.objects.group.Group;
import org.dexenjaeger.algebra.categories.objects.monoid.Monoid;
import org.dexenjaeger.algebra.model.OrderedPair;
import org.dexenjaeger.algebra.utils.BinaryOperatorUtil;

import javax.inject.Inject;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class GroupValidator implements Validator<Group> {
  private final Validator<Monoid> monoidValidator;
  
  @Inject
  public GroupValidator(Validator<Monoid> monoidValidator) {
    this.monoidValidator = monoidValidator;
  }
  
  private ValidationException getCyclesMapException(String reason) {
    return new ValidationException(String.format("Invalid cycles map: %s.", reason));
  }
  
  private void validateCyclesMap(Group item) throws ValidationException {
    Collection<Integer> cycleSizes = item.getCycleSizes();
    if (cycleSizes.isEmpty()) {
      throw getCyclesMapException("map is empty");
    }
    if (cycleSizes.stream().anyMatch(n -> item.getNCycles(n) == null)) {
      throw getCyclesMapException("there exists a null set of nCycles");
    }
    Map<Integer, Set<List<String>>> cyclesMap = cycleSizes.stream()
                                                  .map(n -> new OrderedPair<>(n, item.getNCycles(n)))
                                                  .collect(Collectors.toMap(
                                                    OrderedPair::getLeft,
                                                    OrderedPair::getRight
                                                  ));
    
    for (Integer n:cyclesMap.keySet()) {
      Set<List<String>> nCycles = cyclesMap.get(n);
      if (n < 1) {
        throw getCyclesMapException(String.format(
          "cycle size %d is invalid", n
        ));
      }
      if (nCycles.isEmpty()) {
        throw getCyclesMapException(String.format("the set of %d cycles is empty", n));
      }
      for (List<String> cycle:nCycles) {
        LinkedList<String> linkedCycle = new LinkedList<>(cycle);
        if (linkedCycle.size() != n) {
          throw getCyclesMapException(String.format(
            "there is a %d cycle whose length isn't %d",
            n, n
          ));
        }
        if (!linkedCycle.removeLast().equals(item.getIdentity())) {
          throw getCyclesMapException(String.format(
            "cycle %s doesn't end with identity", cycle
          ));
        }
        String generator = null;
        String previous = null;
        while (!linkedCycle.isEmpty()) {
          String a = linkedCycle.removeFirst();
          if (generator == null) {
            generator = a;
            previous = a;
          } else if (!a.equals(item.prod(generator, previous))) {
            throw getCyclesMapException(String.format(
              "cycle %s is improperly generated",
              cycle
            ));
          } else {
            previous = a;
          }
          if (linkedCycle.isEmpty()) {
            if (!a.equals(item.getInverse(a))) {
              throw getCyclesMapException(String.format(
                "cycle %s doesn't contain the inverse of each element",
                cycle
              ));
            }
          } else if (!linkedCycle.removeLast().equals(item.getInverse(a))) {
            throw getCyclesMapException(String.format(
              "cycle %s doesn't contain the inverse of each element",
              cycle
            ));
          }
        }
      }
    }
  }
  
  private void validateInverses(Group item) throws ValidationException {
    for (String a: item.getElements()) {
      String inverse = Optional.ofNullable(item.getInverse(a))
        .orElseThrow(() -> new ValidationException(String.format(
          "The inverse of element %s not found in Group\n%s",
          a, item.getMultiplicationTable()
        )));
      
      if (!item.getElements().contains(inverse)) {
        throw new ValidationException(String.format(
          "The inverse %s of element %s not found in Group %s",
          inverse, a, BinaryOperatorUtil.getSortedElements(item.getElements(), item.getIdentity())
        ));
      }
      
      if (
        !item.getIdentity().equals(item.prod(a, inverse))
          || !item.getIdentity().equals(item.prod(inverse, a))
      ) {
        throw new ValidationException(String.format(
          "The value %s is not the inverse of the element %s in Group\n%s",
          inverse, a, item.getMultiplicationTable()
        ));
      }
    }
  }
  
  @Override
  public void validate(Group item) throws ValidationException {
    monoidValidator.validate(item);
    validateInverses(item);
    validateCyclesMap(item);
  }
}
