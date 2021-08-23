package org.dexenjaeger.algebra.service;

import org.dexenjaeger.algebra.categories.morphisms.Automorphism;
import org.dexenjaeger.algebra.categories.objects.group.Group;
import org.dexenjaeger.algebra.model.Element;
import org.dexenjaeger.algebra.model.binaryoperator.OperatorSymbol;
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
    } catch (NullPointerException e) {
      throw new ValidationException(String.format(
        "Failed to create inner automorphism from %d", element
      ), e);
    }
  }
  
  public Group createInnerAutomorphismGroup(Group group) {
    List<Automorphism> elements = group.getElementsDisplay().stream()
                                    .map(group::eval)
                                    .map(i -> createInnerAutomorphism(group, i))
                                    .collect(Collectors.toList());
    return groupService.createSortedGroup(
      new GroupSpec()
        .setOperatorSymbol(OperatorSymbol.COMPOSITION)
        .setElements(elements.stream()
                       .map(Automorphism::toString)
                       .map(Element::from)
                       .toArray(Element[]::new))
        .setOperator((i, j) -> elements.indexOf(automorphismService.compose(
          elements.get(i), elements.get(j)
        )))
    ).getGroup();
  }
  
  public Group createOuterAutomorphismGroup(Group group) {
    Group inn = createInnerAutomorphismGroup(group);
    Group aut = automorphismService.createAutomorphismGroup(group);
    
    return groupService.createQuotientGroup(
      aut, inn.getSortedElements()
    );
  }
}
