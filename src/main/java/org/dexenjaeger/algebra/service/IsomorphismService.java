package org.dexenjaeger.algebra.service;

import org.dexenjaeger.algebra.categories.morphisms.ConcreteIsomorphism;
import org.dexenjaeger.algebra.categories.morphisms.Isomorphism;
import org.dexenjaeger.algebra.categories.objects.group.Group;
import org.dexenjaeger.algebra.model.cycle.StringCycle;
import org.dexenjaeger.algebra.validators.IsomorphismValidator;
import org.dexenjaeger.algebra.validators.ValidationException;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class IsomorphismService {
  private final HomomorphismService homomorphismService;
  private final IsomorphismValidator automorphismValidator;
  
  @Inject
  public IsomorphismService(HomomorphismService homomorphismService, IsomorphismValidator automorphismValidator) {
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
  
  public Isomorphism createIsomorphism(
    Group domain, Group range, Function<Integer, Integer> func, Function<Integer, Integer> inverse
  ) throws ValidationException {
    Isomorphism automorphism = ConcreteIsomorphism.builder()
                                  .domain(domain)
                                  .range(range)
                                  .act(func)
                                  .inverseAct(inverse)
                                  .build();
    automorphismValidator.validate(automorphism);
    return automorphism;
  }
  
  public Isomorphism createIsomorphism(
    Group domain, Function<Integer, String> func
  ) throws ValidationException {
    Map<String, Integer> rangeLookup = new HashMap<>();
    String[] elements = new String[domain.getSize()];
    for (int i = 0; i < domain.getSize(); i++) {
      String y = func.apply(i);
      elements[i] = y;
      rangeLookup.put(y, i);
    }
    
    Map<Integer, Integer> rangeInverseMap = rangeLookup.values().stream().collect(Collectors.toMap(
      Function.identity(),
      domain::getInverse
    ));
    
    return createIsomorphism(
      domain,
      Group.builder()
        .inversesMap(rangeInverseMap)
        .maximalCycles(
          domain.getMaximalCycles().stream()
            .map(StringCycle::getElements)
            .map(domainEls -> domainEls.stream()
                                .map(el -> func.apply(domain.eval(el)))
                                .collect(Collectors.toList()))
            .map(rangeEls -> StringCycle.builder().elements(rangeEls).build())
            .collect(Collectors.toSet())
        )
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
