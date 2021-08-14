package org.dexenjaeger.algebra.validators;

import org.dexenjaeger.algebra.categories.objects.group.Group;
import org.dexenjaeger.algebra.categories.objects.monoid.Monoid;
import org.dexenjaeger.algebra.model.cycle.IntCycle;
import org.dexenjaeger.algebra.utils.MoreMath;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class GroupValidator implements Validator<Group> {
  private final Validator<Monoid> monoidValidator;
  
  @Inject
  public GroupValidator(Validator<Monoid> monoidValidator) {
    this.monoidValidator = monoidValidator;
  }
  
  private ValidationException getCycleException(String reason) {
    return new ValidationException(String.format("Invalid cycle: %s.", reason));
  }
  
  private ValidationException getMaximalCyclesException(String reason) {
    return new ValidationException(String.format("Invalid maximal cycles: %s.", reason));
  }
  
  private Optional<ValidationException> validateCycle(Group item, IntCycle cycle) {
    try {
      validateCycleElements(item, cycle.getElements());
      cycle.getSubCycles().forEach(subcycle -> validateCycle(item, subcycle));
    } catch (ValidationException e) {
      return Optional.of(e);
    }
    return Optional.empty();
  }
  
  private void validateCycleElements(Group item, List<Integer> cycleElements) throws ValidationException {
  
    if (cycleElements.isEmpty()) {
      throw getCycleException("cycle is empty");
    }
    LinkedList<Integer> linkedCycle = new LinkedList<>(cycleElements);
    if (!linkedCycle.removeLast().equals(item.getIdentity())) {
      throw getCycleException(String.format(
        "cycle %s doesn't end with identity", cycleElements
      ));
    }
    Integer generator = null;
    Integer previous = null;
    while (!linkedCycle.isEmpty()) {
      Integer a = linkedCycle.removeFirst();
      if (generator == null) {
        generator = a;
        previous = a;
      } else if (!a.equals(item.prod(generator, previous))) {
        throw getCycleException(String.format(
          "cycle %s is improperly generated",
          cycleElements
        ));
      } else {
        previous = a;
      }
      if (linkedCycle.isEmpty()) {
        if (!a.equals(item.getInverse(a))) {
          throw getCycleException(String.format(
            "cycle %s doesn't contain the inverse of each element",
            cycleElements
          ));
        }
      } else if (!linkedCycle.removeLast().equals(item.getInverse(a))) {
        throw getCycleException(String.format(
          "cycle %s doesn't contain the inverse of each element",
          cycleElements
        ));
      }
    }
  }
  
  private void validateMaximalCycles(Group item) throws ValidationException {
    Set<IntCycle> maximalCycles = item.getMaximalCycles();
    if (maximalCycles.isEmpty()) {
      throw getMaximalCyclesException("there is an empty maximal cycle");
    }
    Set<Integer> coveredElements = new HashSet<>();
    for (IntCycle cycle:maximalCycles) {
      Set<Integer> intersection = MoreMath.intersection(coveredElements, cycle.getElements());
      if (intersection.size() > 1) {
        throw getMaximalCyclesException(String.format(
          "cycle %s intersects with the other maximal cycles in more than one element %s",
            cycle, intersection
          ));
      }
      Optional<ValidationException> e = validateCycle(item, cycle);
      if (e.isPresent()) {
        throw e.get();
      }
    }
  }
  
  private void validateInverses(Group item) throws ValidationException {
    for (String a: item.getElementsDisplay()) {
      String inverse;
      try {
        inverse = item.getInverse(a);
      } catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
        throw new ValidationException(String.format(
          "The inverse of element %s not found in inverses map for Group\n%s",
          a, item
        ));
      }
      
      if (
        !item.getIdentityDisplay().equals(item.prod(a, inverse))
          || !item.getIdentityDisplay().equals(item.prod(inverse, a))
      ) {
        throw new ValidationException(String.format(
          "The value %s is not the inverse of the element %s in Group\n%s",
          inverse, a, item.printMultiplicationTable()
        ));
      }
    }
  }
  
  @Override
  public void validate(Group item) throws ValidationException {
    monoidValidator.validate(item);
    validateInverses(item);
    validateMaximalCycles(item);
  }
}
