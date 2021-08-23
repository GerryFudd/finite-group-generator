package org.dexenjaeger.algebra.service;

import org.dexenjaeger.algebra.categories.morphisms.Isomorphism;
import org.dexenjaeger.algebra.categories.objects.group.Group;
import org.dexenjaeger.algebra.model.Element;
import org.dexenjaeger.algebra.model.Mapping;
import org.dexenjaeger.algebra.model.binaryoperator.OperatorSymbol;
import org.dexenjaeger.algebra.model.spec.GroupSpec;
import org.dexenjaeger.algebra.utils.FunctionsUtil;
import org.dexenjaeger.algebra.validators.Validator;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class IsomorphismService {
  private final FunctionsUtil functionsUtil;
  private final GroupService groupService;
  private final Validator<Isomorphism> automorphismValidator;
  
  @Inject
  public IsomorphismService(
    FunctionsUtil functionsUtil,
    GroupService groupService,
    Validator<Isomorphism> automorphismValidator
  ) {
    this.functionsUtil = functionsUtil;
    this.groupService = groupService;
    this.automorphismValidator = automorphismValidator;
  }
  
  public Isomorphism createIsomorphism(
    Group domain, Group range, Function<Integer, Integer> func, Function<Integer, Integer> inverse
  ) {
    Mapping mapping = functionsUtil.createMapping(domain.getSize(), func);
    Isomorphism automorphism = Isomorphism.builder()
                                 .inverseMapping(functionsUtil.createMapping(domain.getSize(), inverse).getArray())
                                 .domain(domain)
                                 .range(range)
                                 .image(functionsUtil.createImage(
                                   domain.getSize(), i -> range.display(mapping.get(i))
                                 ))
                                 .mapping(mapping.getArray())
                                 .build();
    automorphismValidator.validate(automorphism);
    return automorphism;
  }
  
  public Isomorphism createIsomorphism(
    Group domain, Function<Integer, Element> func
  ) {
    Map<Element, Integer> rangeLookup = new HashMap<>();
    Element[] elements = new Element[domain.getSize()];
    for (int i = 0; i < domain.getSize(); i++) {
      Element y = func.apply(i);
      elements[i] = y;
      rangeLookup.put(y, i);
    }
    
    Map<Integer, Integer> rangeInverseMap = rangeLookup.values().stream().collect(Collectors.toMap(
      Function.identity(),
      domain::getInverse
    ));
    
    return createIsomorphism(
      domain,
      groupService.createGroup(
        new GroupSpec()
        .setOperatorSymbol(OperatorSymbol.ALTERNATE)
        .setIdentity(domain.getIdentity())
        .setElements(elements)
        .setInversesMap(rangeInverseMap)
        .setMaximalCycles(domain.getMaximalCycles())
        .setOperator(domain::prod)
      ),
      Function.identity(),
      Function.identity()
    );
  }
  
  public Isomorphism getInverse(Isomorphism isomorphism) {
    return Isomorphism.builder()
             .inverseMapping(functionsUtil.createMapping(
               isomorphism.getDomain().getSize(), isomorphism::apply
             ).getArray())
             .mapping(functionsUtil.createMapping(
               isomorphism.getDomain().getSize(), isomorphism::unApply
             ).getArray())
             .domain(isomorphism.getRange())
             .range(isomorphism.getDomain())
             .image(functionsUtil.createImage(
               isomorphism.getDomain().getSize(),
               i -> isomorphism.getDomain().display(isomorphism.unApply(i))
             ))
      .build();
  }
}
