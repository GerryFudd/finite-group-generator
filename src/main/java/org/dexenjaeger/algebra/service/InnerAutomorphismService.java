package org.dexenjaeger.algebra.service;

import org.dexenjaeger.algebra.categories.morphisms.Automorphism;
import org.dexenjaeger.algebra.categories.objects.group.Group;
import org.dexenjaeger.algebra.utils.BinaryOperatorUtil;
import org.dexenjaeger.algebra.validators.ValidationException;

import javax.inject.Inject;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
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
    return automorphismService.create(
      group, a -> group.prod(element, group.prod(a, group.getInverse(element)))
    );
  }
  
  public Group createInnerAutomorphismGroup(Group group) throws ValidationException {
    Set<Automorphism> elements = group.getElementsDisplay().stream()
      .map(group::eval)
      .map(i -> createInnerAutomorphism(group, i))
      .collect(Collectors.toSet());
    Automorphism identity = elements.stream()
      .filter(aut -> aut.fixedElements().size() == group.getSize())
      .findAny().orElseThrow();
    Comparator<String> comparator = BinaryOperatorUtil.getElementComparator(identity.toString());
    List<Automorphism> elementsList = elements.stream()
      .sorted((e1, e2) -> comparator.compare(e1.toString(), e2.toString()))
      .collect(Collectors.toList());
    int[][] multiplicationTable = new int[elements.size()][elements.size()];
    for (int i = 0; i < elements.size(); i++) {
      for (int j = 0; j < elements.size(); j++) {
        multiplicationTable[i][j] = elementsList.indexOf(automorphismService.compose(
          elementsList.get(i), elementsList.get(j)
        ));
      }
    }
    return groupService.constructGroupFromElementsAndMultiplicationTable(
      "o",
      elementsList.stream().map(Automorphism::toString).toArray(String[]::new),
      multiplicationTable
    );
  }
}
