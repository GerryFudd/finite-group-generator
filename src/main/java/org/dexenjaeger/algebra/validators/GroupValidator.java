package org.dexenjaeger.algebra.validators;

import org.dexenjaeger.algebra.categories.objects.group.Group;
import org.dexenjaeger.algebra.categories.objects.monoid.Monoid;
import org.dexenjaeger.algebra.model.cycle.IntCycle;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.LinkedList;
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
  
  private void validateMaximalCycles(Group item) {
    Set<IntCycle> maximalCycles = item.getMaximalCycles();
    Set<Integer> coveredElements = new HashSet<>();
    for (IntCycle cycle:maximalCycles) {
      LinkedList<Integer> linkedCycle = new LinkedList<>(cycle.getElements());
      Integer generator = null;
      Integer previous = null;
      while (!linkedCycle.isEmpty()) {
        Integer a = linkedCycle.removeFirst();
        if (a < 0 || item.getSize() <= a) {
          throw getCycleException(String.format(
            "cycle contains %s, which is outside group",
            a
          ));
        }
        if (!coveredElements.add(a) && item.getIdentity() != a) {
          throw getCycleException(String.format(
            "%d is covered by more than one maximal cycle", a
          ));
        }
        if (generator == null) {
          generator = a;
          previous = a;
        } else if (!a.equals(item.prod(generator, previous))) {
          throw getCycleException(String.format(
            "cycle %s is improperly generated",
            cycle
          ));
        } else {
          previous = a;
        }
      }
  
      if (generator == null || generator != item.prod(
        generator,
        previous
      )) {
        throw getCycleException(String.format("cycle %s ends prematurely", cycle));
      }
    }
    if (coveredElements.size() < item.getSize()) {
      throw new ValidationException(
        "Cycles don't cover group."
      );
    }
  }
  
  private void validateInverses(Group item) {
    for (int i = 0; i < item.getSize(); i++) {
      int inverse;
      try {
        inverse = item.getInverse(i);
      } catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
        throw new ValidationException(String.format(
          "The inverse of element %d not found in inverses map for Group\n%s",
          i, item
        ));
      }
      
      if (inverse >= item.getSize()) {
        throw new ValidationException(String.format(
          "The value %d is not the inverse of the element %d in Group\n%s",
          inverse, i, item.printMultiplicationTable()
        ));
      }
      
      if (item.getIdentity() != item.prod(i, inverse)
          || item.getIdentity() != item.prod(inverse, i)
      ) {
        throw new ValidationException(String.format(
          "The value %s is not the inverse of the element %s in Group\n%s",
          item.display(inverse), item.display(i), item.printMultiplicationTable()
        ));
      }
    }
  }
  
  @Override
  public void validate(Group item) {
    monoidValidator.validate(item);
    validateInverses(item);
    validateMaximalCycles(item);
  }
}
