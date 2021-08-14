package org.dexenjaeger.algebra.service;

import org.dexenjaeger.algebra.categories.morphisms.Automorphism;
import org.dexenjaeger.algebra.categories.objects.group.Group;
import org.dexenjaeger.algebra.utils.BinaryOperatorUtil;
import org.dexenjaeger.algebra.validators.ValidationException;
import org.dexenjaeger.algebra.validators.Validator;

import javax.inject.Inject;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class InnerAutomorphismService {
  private final Validator<Automorphism> automorphismValidator;
  private final GroupService groupService;
  private final HomomorphismService homomorphismService;
  private final AutomorphismService automorphismService;
  
  @Inject
  public InnerAutomorphismService(
    Validator<Automorphism> automorphismValidator, GroupService groupService,
    HomomorphismService homomorphismService, AutomorphismService automorphismService
  ) {
    this.automorphismValidator = automorphismValidator;
    this.groupService = groupService;
    this.homomorphismService = homomorphismService;
    this.automorphismService = automorphismService;
  }
  
  public Automorphism createInnerAutomorphism(Group group, int i) {
    Automorphism result = Automorphism.builder()
                            .domain(group)
                            .act(a -> group.prod(i, group.prod(a, group.getInverse(i))))
                            .build();
    try {
      automorphismValidator.validate(result);
    } catch (ValidationException e) {
      throw new RuntimeException("Result is not a valid automorphism.", e);
    }
    return result;
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
