package org.dexenjaeger.algebra.service;

import org.dexenjaeger.algebra.categories.morphisms.Automorphism;
import org.dexenjaeger.algebra.categories.objects.group.Group;
import org.dexenjaeger.algebra.model.spec.GroupSpec;
import org.dexenjaeger.algebra.validators.ValidationException;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

public class InnerAutomorphismService {
  private final GroupService groupService;
  private final AutomorphismService automorphismService;
  
  @Inject
  public InnerAutomorphismService(
    GroupService groupService, AutomorphismService automorphismService
  ) {
    this.groupService = groupService;
    this.automorphismService = automorphismService;
  }
  
  public Automorphism createInnerAutomorphism(Group group, int element) {
    try {
      return automorphismService.createAutomorphism(
        group, a -> group.prod(element, group.prod(a, group.getInverse(element)))
      );
    } catch (ValidationException | NullPointerException e) {
      throw new RuntimeException(String.format(
        "Failed to create inner automorphism from %d", element
      ), e);
    }
  }
  
  public Group createInnerAutomorphismGroup(Group group) throws ValidationException {
    List<Automorphism> elements = group.getElementsDisplay().stream()
      .map(group::eval)
      .map(i -> createInnerAutomorphism(group, i))
      .collect(Collectors.toList());
    return groupService.createSortedGroup(
      new GroupSpec()
      .setOperatorSymbol("o")
      .setElements(elements.stream().map(Automorphism::toString).toArray(String[]::new))
      .setOperator((i, j) -> {
        try {
          return elements.indexOf(automorphismService.compose(
            elements.get(i), elements.get(j)
          ));
        } catch (ValidationException e) {
          throw new RuntimeException(String.format(
            "Failed to evaluate binary operator (%d, %d)",
            i, j
          ));
        }
      })
    ).getGroup();
  }
}
