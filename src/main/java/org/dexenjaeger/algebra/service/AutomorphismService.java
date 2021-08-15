package org.dexenjaeger.algebra.service;

import org.dexenjaeger.algebra.categories.morphisms.Automorphism;
import org.dexenjaeger.algebra.categories.morphisms.AutomorphismBuilder;
import org.dexenjaeger.algebra.categories.objects.group.Group;
import org.dexenjaeger.algebra.model.cycle.StringCycle;
import org.dexenjaeger.algebra.utils.FunctionsUtil;
import org.dexenjaeger.algebra.validators.ValidationException;
import org.dexenjaeger.algebra.validators.Validator;

import javax.inject.Inject;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AutomorphismService {
  private final Validator<Automorphism> automorphismValidator;
  private final FunctionsUtil functionsUtil;
  
  @Inject
  public AutomorphismService(
    Validator<Automorphism> automorphismValidator,
    FunctionsUtil functionsUtil
  ) {
    this.automorphismValidator = automorphismValidator;
    this.functionsUtil = functionsUtil;
  }
  
  public Automorphism compose(Automorphism a, Automorphism b) throws ValidationException {
    if (!a.getDomain().equals(b.getDomain())) {
      throw new RuntimeException("No.");
    }
    Automorphism result = createAutomorphism(
      b.getDomain(), i -> a.apply(b.apply(i))
    );
    automorphismValidator.validate(result);
    return result;
  }
  
  public Automorphism createAutomorphism(Group domain, Function<Integer, Integer> act) {
    int[] mapping = functionsUtil.createMapping(domain.getSize(), act);
    
    AutomorphismBuilder resultBuilder = Automorphism.builder();
    
    Set<String> remainingElements = new HashSet<>(domain.getElementsDisplay());
    while (!remainingElements.isEmpty()) {
      String seed = remainingElements.stream().findAny().orElseThrow();
      LinkedList<String> currentCycle = new LinkedList<>();
      currentCycle.addLast(seed);
      remainingElements.remove(seed);
      String current = domain.display(act.apply(domain.eval(seed)));
      while (!seed.equals(current)) {
        currentCycle.addLast(current);
        remainingElements.remove(current);
        current = domain.display(act.apply(domain.eval(current)));
      }
      if (currentCycle.size() > 1) {
        resultBuilder.withStringCycles(
          StringCycle.builder().elements(currentCycle).build()
        );
      }
    }
    
    Automorphism result = resultBuilder
                            .fixedElements(functionsUtil.getFixedElements(
                              domain.getSize(), i -> mapping[i]
                            ))
                            .inverseMapping(functionsUtil.createInverseMapping(
                              domain.getSize(), i -> mapping[i]
                            ))
                            .domain(domain)
                            .mapping(mapping)
                            .image(functionsUtil.createImage(
                              domain.getSize(), i -> domain.display(mapping[i])
                            ))
                            .build();
    
    try {
      automorphismValidator.validate(result);
    } catch (ValidationException e) {
      throw new RuntimeException("Result is not a valid automorphism.", e);
    }
    return result;
  }
  
  private Set<StringCycle> convertCycles(Collection<StringCycle> cycles, Function<String, String> func) {
    return cycles.stream()
             .map(cycle -> cycle.getElements()
                             .stream()
                             .map(func)
                             .collect(Collectors.toList()))
             .map(elements -> StringCycle.builder()
                                .elements(elements)
                                .build())
             .collect(Collectors.toSet());
  }
  
  public Automorphism getInverse(Automorphism automorphism) {
    return Automorphism.builder()
             .withStringCycles(convertCycles(
               automorphism.getCyclePresentation().getCycles(),
               automorphism::unApply
             ))
             .fixedElements(automorphism.getFixedElements())
             .inverseMapping(functionsUtil.createMapping(
               automorphism.getDomain().getSize(),
               automorphism::apply
             ))
             .mapping(functionsUtil.createMapping(
               automorphism.getDomain().getSize(),
               automorphism::unApply
             ))
             .image(functionsUtil.createImage(
               automorphism.getDomain().getSize(),
               i -> automorphism.getDomain().display(automorphism.unApply(i))
             ))
             .build();
  }
}
