package org.dexenjaeger.algebra.service;

import com.google.common.collect.Sets;
import org.dexenjaeger.algebra.categories.morphisms.Automorphism;
import org.dexenjaeger.algebra.categories.morphisms.AutomorphismBuilder;
import org.dexenjaeger.algebra.categories.objects.group.Group;
import org.dexenjaeger.algebra.categories.objects.group.TrivialGroup;
import org.dexenjaeger.algebra.model.Element;
import org.dexenjaeger.algebra.model.Mapping;
import org.dexenjaeger.algebra.model.cycle.ElementCycle;
import org.dexenjaeger.algebra.model.spec.GroupSpec;
import org.dexenjaeger.algebra.utils.CycleUtils;
import org.dexenjaeger.algebra.utils.FunctionsUtil;
import org.dexenjaeger.algebra.validators.ValidationException;
import org.dexenjaeger.algebra.validators.Validator;

import javax.inject.Inject;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
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
    
    Set<Element> remainingElements = new HashSet<>(domain.getElementsDisplay());
    while (!remainingElements.isEmpty()) {
      Element seed = remainingElements.stream().findAny().orElseThrow();
      LinkedList<Element> currentCycle = new LinkedList<>();
      currentCycle.addLast(seed);
      remainingElements.remove(seed);
      Element current = domain.display(act.apply(domain.eval(seed)));
      while (!seed.equals(current) && !domain.getIdentityDisplay().equals(current)) {
        currentCycle.addLast(current);
        remainingElements.remove(current);
        current = domain.display(act.apply(domain.eval(current)));
      }
      if (currentCycle.size() > 1) {
        resultBuilder.withStringCycles(
          cycleUtils.createElementCycle(currentCycle)
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
  
  private Set<ElementCycle> convertCycles(Collection<ElementCycle> cycles, Function<Element, Element> func) {
    return cycles.stream()
             .map(cycle -> cycle.getElements()
                             .stream()
                             .map(func)
                             .collect(Collectors.toList()))
             .map(cycleUtils::createElementCycle)
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
  
  private Optional<Automorphism> createPossibleAutomorphism(
    Group group,
    Map<Integer, Integer> cycleGeneratorMapping
  ) {
    int[] newMapping = new int[group.getSize()];
    newMapping[group.getIdentity()] = group.getIdentity();
    Set<Integer> mapped = Sets.newHashSet(group.getIdentity());
    Set<Integer> mappedTo = Sets.newHashSet(group.getIdentity());
    for (Map.Entry<Integer, Integer> generatorMapping:cycleGeneratorMapping.entrySet()) {
      int mappedGenerator = generatorMapping.getKey();
      int imageGenerator = generatorMapping.getValue();
      if (!mappedTo.add(imageGenerator)) {
        return Optional.empty();
      }
      if (mapped.add(mappedGenerator)) {
        newMapping[mappedGenerator] = imageGenerator;
      } else if (newMapping[mappedGenerator] != imageGenerator) {
        return Optional.empty();
      }
      int nextCycleElement = group.prod(mappedGenerator, mappedGenerator);
      int nextImageElement = group.prod(imageGenerator, imageGenerator);
      while (nextCycleElement != group.getIdentity()) {
        if (!mappedTo.add(nextImageElement)) {
          return Optional.empty();
        }
        if (mapped.add(nextCycleElement)) {
          newMapping[nextCycleElement] = nextImageElement;
        } else {
          if (newMapping[nextCycleElement] != nextImageElement) {
            return Optional.empty();
          }
        }
        nextCycleElement = group.prod(mappedGenerator, nextCycleElement);
        nextImageElement = group.prod(imageGenerator, nextImageElement);
      }
    }
    if (mapped.size() < group.getSize()) {
      return Optional.empty();
    }
    try {
      return Optional.of(createAutomorphism(group, x -> newMapping[x]));
    } catch (ValidationException | NullPointerException e) {
      return Optional.empty();
    }
  }
  
  public Group createAutomorphismGroup(Group group) {
    if (group.getSize() < 2) {
      return new TrivialGroup();
    }
    LinkedList<Automorphism> automorphisms = new LinkedList<>();
    
    AutomorphismSeedIterable automorphismSeedIterable = AutomorphismSeedIterable.init(group.getMaximalCycles());
    
    for (Map<Integer, Integer> automorphismSeed:automorphismSeedIterable) {
      Optional<Automorphism> possibleAutomorphism = createPossibleAutomorphism(
        group, automorphismSeed
      );
      possibleAutomorphism.ifPresent(automorphisms::addLast);
    }
    
    return groupService.createSortedGroup(
      new GroupSpec()
        .setElements(automorphisms.stream()
                       .map(Automorphism::toString)
          .map(Element::from)
                       .toArray(Element[]::new))
        .setOperator((i, j) -> automorphisms.indexOf(doCompose(
          automorphisms.get(i), automorphisms.get(j)
        )))
    ).getGroup();
  }
}
