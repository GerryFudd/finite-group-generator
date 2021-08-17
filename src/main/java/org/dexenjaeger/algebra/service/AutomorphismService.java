package org.dexenjaeger.algebra.service;

import org.dexenjaeger.algebra.categories.morphisms.Automorphism;
import org.dexenjaeger.algebra.categories.morphisms.AutomorphismBuilder;
import org.dexenjaeger.algebra.categories.objects.group.Group;
import org.dexenjaeger.algebra.categories.objects.group.TrivialGroup;
import org.dexenjaeger.algebra.model.Mapping;
import org.dexenjaeger.algebra.model.cycle.StringCycle;
import org.dexenjaeger.algebra.model.spec.GroupSpec;
import org.dexenjaeger.algebra.utils.CycleUtils;
import org.dexenjaeger.algebra.utils.FunctionsUtil;
import org.dexenjaeger.algebra.utils.PermutationUtil;
import org.dexenjaeger.algebra.validators.ValidationException;
import org.dexenjaeger.algebra.validators.Validator;

import javax.inject.Inject;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AutomorphismService {
  private final CycleUtils cycleUtils;
  private final Validator<Automorphism> automorphismValidator;
  private final FunctionsUtil functionsUtil;
  private final GroupService groupService;
  
  @Inject
  public AutomorphismService(
    CycleUtils cycleUtils,
    Validator<Automorphism> automorphismValidator,
    FunctionsUtil functionsUtil,
    GroupService groupService
  ) {
    this.cycleUtils = cycleUtils;
    this.automorphismValidator = automorphismValidator;
    this.functionsUtil = functionsUtil;
    this.groupService = groupService;
  }
  
  public Automorphism compose(Automorphism a, Automorphism b) {
    if (!a.getDomain().equals(b.getDomain())) {
      throw new RuntimeException("No.");
    }
    Automorphism result = createAutomorphism(
      b.getDomain(), i -> a.apply(b.apply(i))
    );
    automorphismValidator.validate(result);
    return result;
  }
  
  private Automorphism doCompose(Automorphism a, Automorphism b) {
    return doCreateAutomorphism(
      b.getDomain(), i -> a.apply(b.apply(i))
    );
  }
  
  public Automorphism createAutomorphism(Group domain, Function<Integer, Integer> act) {
    Automorphism result = doCreateAutomorphism(domain, act);
    
    automorphismValidator.validate(result);
    return result;
  }
  
  private Automorphism doCreateAutomorphism(Group domain, Function<Integer, Integer> act) {
    Mapping mapping = functionsUtil.createMapping(domain.getSize(), act);
    
    AutomorphismBuilder resultBuilder = Automorphism.builder();
    
    Set<String> remainingElements = new HashSet<>(domain.getElementsDisplay());
    while (!remainingElements.isEmpty()) {
      String seed = remainingElements.stream().findAny().orElseThrow();
      LinkedList<String> currentCycle = new LinkedList<>();
      currentCycle.addLast(seed);
      remainingElements.remove(seed);
      String current = domain.display(act.apply(domain.eval(seed)));
      while (!seed.equals(current) && !domain.getIdentityDisplay().equals(current)) {
        currentCycle.addLast(current);
        remainingElements.remove(current);
        current = domain.display(act.apply(domain.eval(current)));
      }
      if (currentCycle.size() > 1) {
        resultBuilder.withStringCycles(
          cycleUtils.createStringCycle(currentCycle)
        );
      }
    }
    
    return resultBuilder
             .identity(mapping.isIdentity())
             .fixedElements(functionsUtil.getFixedElements(
               domain.getSize(), mapping::get
             ))
             .inverseMapping(functionsUtil.createInverseMapping(
               domain.getSize(), mapping::get
             ))
             .domain(domain)
             .mapping(mapping.getArray())
             .image(functionsUtil.createImage(
               domain.getSize(), i -> domain.display(mapping.get(i))
             ))
             .build();
  }
  
  private Set<StringCycle> convertCycles(Collection<StringCycle> cycles, Function<String, String> func) {
    return cycles.stream()
             .map(cycle -> cycle.getElements()
                             .stream()
                             .map(func)
                             .collect(Collectors.toList()))
             .map(cycleUtils::createStringCycle)
             .collect(Collectors.toSet());
  }
  
  public Automorphism getInverse(Automorphism automorphism) {
    Automorphism result =  Automorphism.builder()
                             .withStringCycles(convertCycles(
                               automorphism.getCyclePresentation().getCycles(),
                               automorphism::unApply
                             ))
                             .fixedElements(automorphism.getFixedElements())
                             .inverseMapping(functionsUtil.createMapping(
                               automorphism.getDomain().getSize(),
                               automorphism::apply
                             ).getArray())
                             .domain(automorphism.getDomain())
                             .mapping(functionsUtil.createMapping(
                               automorphism.getDomain().getSize(),
                               automorphism::unApply
                             ).getArray())
                             .image(functionsUtil.createImage(
                               automorphism.getDomain().getSize(),
                               i -> automorphism.getDomain().display(automorphism.unApply(i))
                             ))
                             .build();
    automorphismValidator.validate(result);
    return result;
  }
  
  private Optional<Automorphism> createPotentialAutomorphism(Group group, Mapping mapping) {
    try {
      return Optional.of(createAutomorphism(
        group, i -> {
          if (i == group.getIdentity()) {
            return i;
          }
          if (i < group.getIdentity()) {
            if (mapping.get(i) < group.getIdentity()) {
              return mapping.get(i);
            }
            return mapping.get(i) + 1;
          }
          if (mapping.get(i - 1) < group.getIdentity()) {
            return mapping.get(i - 1);
          }
          return mapping.get(i - 1) + 1;
        }
      ));
    } catch (ValidationException e) {
      System.out.printf("\nMapping %s failed to generate an automorphism.\n", mapping);
      System.out.print(e.getMessage());
      return Optional.empty();
    }
  }
  
  public Group createAutomorphismGroup(Group group) {
    if (group.getSize() < 2) {
      return new TrivialGroup();
    }
    LinkedList<Automorphism> automorphisms = new LinkedList<>();
    
    for (Mapping potentialMapping:PermutationUtil.getPermutationList(group.getSize() - 1)) {
      createPotentialAutomorphism(group, potentialMapping)
        .ifPresent(automorphisms::addLast);
    }
    
    return groupService.createSortedGroup(
      new GroupSpec()
        .setElements(automorphisms.stream().map(Automorphism::toString).toArray(String[]::new))
        .setOperator((i, j) -> automorphisms.indexOf(doCompose(
          automorphisms.get(i), automorphisms.get(j)
        )))
    ).getGroup();
  }
}
