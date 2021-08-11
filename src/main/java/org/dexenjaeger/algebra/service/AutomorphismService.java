package org.dexenjaeger.algebra.service;

import org.dexenjaeger.algebra.categories.morphisms.Automorphism;
import org.dexenjaeger.algebra.categories.morphisms.ConcreteAutomorphism;
import org.dexenjaeger.algebra.categories.objects.group.ConcreteGroup;
import org.dexenjaeger.algebra.categories.objects.group.Group;
import org.dexenjaeger.algebra.model.OrderedPair;
import org.dexenjaeger.algebra.validators.AutomorphismValidator;
import org.dexenjaeger.algebra.validators.ValidationException;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AutomorphismService {
  private final HomomorphismService homomorphismService;
  private final AutomorphismValidator automorphismValidator;
  
  @Inject
  public AutomorphismService(HomomorphismService homomorphismService, AutomorphismValidator automorphismValidator) {
    this.homomorphismService = homomorphismService;
    this.automorphismValidator = automorphismValidator;
  }
  
  private Set<List<String>> convertCycles(Set<List<String>> cycles, Function<String, String> func) {
    return cycles.stream()
             .map(cycle -> cycle.stream()
                             .map(func)
                             .collect(Collectors.toList()))
             .collect(Collectors.toSet());
  }
  
  public Automorphism createAutomorphism(
    Group domain, Group range, Function<Integer, Integer> func, Function<Integer, Integer> inverse
  ) throws ValidationException {
    Automorphism automorphism = ConcreteAutomorphism.builder()
                                  .domain(domain)
                                  .range(range)
                                  .act(func)
                                  .inverseAct(inverse)
                                  .build();
    automorphismValidator.validate(automorphism);
    return automorphism;
  }
  
  public Automorphism createAutomorphism(
    Group domain, Function<Integer, String> func
  ) throws ValidationException {
    Map<String, Integer> rangeLookup = new HashMap<>();
    String[] elements = new String[domain.getSize()];
    for (int i = 0; i < domain.getSize(); i++) {
      String y = func.apply(i);
      elements[i] = y;
      rangeLookup.put(y, i);
    }
    
    Function<String, String> displayFunc = x -> func.apply(domain.eval(x));
    
    Map<Integer, Integer> rangeInverseMap = rangeLookup.values().stream().collect(Collectors.toMap(
      Function.identity(),
      domain::getInverse
    ));
    
    return createAutomorphism(
      domain,
      ConcreteGroup.builder()
        .inversesMap(rangeInverseMap)
        .cyclesMap(domain
                     .getCycleSizes()
                     .stream()
                     .map(n -> new OrderedPair<>(
                       n, convertCycles(domain.getNCycles(n), displayFunc)
                     ))
                     .collect(Collectors.toMap(
                       OrderedPair::getLeft,
                       OrderedPair::getRight
                     )))
        .identity(domain.getIdentity())
        .operatorSymbol("x")
        .elements(elements)
        .operator(domain::prod)
        .build(),
      Function.identity(),
      Function.identity()
    );
  }
}
