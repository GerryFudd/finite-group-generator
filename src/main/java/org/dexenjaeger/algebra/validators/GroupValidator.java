package org.dexenjaeger.algebra.validators;

import lombok.RequiredArgsConstructor;
import org.dexenjaeger.algebra.categories.objects.group.Group;
import org.dexenjaeger.algebra.categories.objects.monoid.Monoid;
import org.dexenjaeger.algebra.model.OrderedPair;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class GroupValidator implements Validator<Group> {
  private final Validator<Monoid> monoidValidator;
  
  private ValidationException getCyclesMapException(String reason) {
    return new ValidationException(String.format("Invalid cycles map: %s.", reason));
  }
  
  private void validateCyclesMap(Group item) throws ValidationException {
    Collection<Integer> cycleSizes = item.getCycleSizes();
    if (cycleSizes == null || cycleSizes.isEmpty()) {
      throw getCyclesMapException("map is null");
    }
    Map<Integer, Set<List<String>>> cyclesMap = cycleSizes.stream()
                                                  .map(n -> new OrderedPair<>(n, item.getNCycles(n)))
                                                  .collect(Collectors.toMap(
                                                    OrderedPair::getLeft,
                                                    OrderedPair::getRight
                                                  ));
    for (Integer n:cyclesMap.keySet()) {
      for (List<String> cycle:cyclesMap.get(n)) {
        LinkedList<String> linkedCycle = new LinkedList<>(cycle);
        if (linkedCycle.isEmpty()) {
          throw getCyclesMapException("there is an empty cycle");
        }
        if (!linkedCycle.removeLast().equals(item.getIdentity())) {
          throw getCyclesMapException("cycle doesn't end with identity");
        }
        String generator = null;
        String previous = null;
        while (!linkedCycle.isEmpty()) {
          String a = linkedCycle.removeFirst();
          if (generator == null) {
            generator = a;
            previous = a;
          } else if (!a.equals(item.prod(generator, previous))) {
            throw getCyclesMapException("cycle is improperly generated");
          } else {
            previous = a;
          }
          if (a.equals(item.getIdentity())) {
            throw getCyclesMapException("cycle contains identity in middle");
          }
          if (linkedCycle.isEmpty()) {
            if (!a.equals(item.getInverse(a))) {
              throw getCyclesMapException("cycle doesn't contain the inverse of each element");
            }
          } else if (!linkedCycle.removeLast().equals(item.getInverse(a))) {
            throw getCyclesMapException("cycle doesn't contain the inverse of each element");
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
