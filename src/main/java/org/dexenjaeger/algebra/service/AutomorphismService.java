package org.dexenjaeger.algebra.service;

import org.dexenjaeger.algebra.categories.morphisms.Automorphism;
import org.dexenjaeger.algebra.categories.morphisms.ConcreteAutomorphism;
import org.dexenjaeger.algebra.categories.objects.group.ConcreteGroup;
import org.dexenjaeger.algebra.categories.objects.group.Group;
import org.dexenjaeger.algebra.model.OrderedPair;
import org.dexenjaeger.algebra.validators.AutomorphismValidator;
import org.dexenjaeger.algebra.validators.ValidationException;

import javax.inject.Inject;
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
    Group domain, Group range, Function<String, String> func, Function<String, String> inverse
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
    Group domain, Function<String, String> func
  ) throws ValidationException {
    Map<String, String> inverseFuncMap = domain.getElementsDisplay().stream()
                                           .collect(Collectors.toMap(
                                             func,
                                             Function.identity()
                                           ));
    
    Map<String, String> rangeInverseMap = inverseFuncMap.entrySet().stream().collect(Collectors.toMap(
      Map.Entry::getKey,
      entry -> func.apply(domain.getInverse(entry.getValue()))
    ));
    
    return createAutomorphism(
      domain,
      ConcreteGroup.builder()
        .operatorSymbol("x")
        .identityDisplay(func.apply(domain.getIdentityDisplay()))
        .elementsDisplay(inverseFuncMap.keySet())
        .displayInversesMap(rangeInverseMap)
        .cyclesMap(domain
                     .getCycleSizes()
                     .stream()
                     .map(n -> new OrderedPair<>(
                       n, convertCycles(domain.getNCycles(n), func)
                     ))
                     .collect(Collectors.toMap(
                       OrderedPair::getLeft,
                       OrderedPair::getRight
                     )))
        .displayOperator((a, b) -> func.apply(domain.prod(
          inverseFuncMap.get(a),
          inverseFuncMap.get(b)
        )))
        .build(),
      func,
      inverseFuncMap::get
    );
  }
}
