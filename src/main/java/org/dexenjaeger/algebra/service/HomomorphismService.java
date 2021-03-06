package org.dexenjaeger.algebra.service;

import org.dexenjaeger.algebra.categories.morphisms.Homomorphism;
import org.dexenjaeger.algebra.categories.objects.group.Group;
import org.dexenjaeger.algebra.model.HomomorphismSummary;
import org.dexenjaeger.algebra.model.SortedGroupResult;
import org.dexenjaeger.algebra.model.binaryoperator.Element;
import org.dexenjaeger.algebra.model.binaryoperator.OperatorSymbol;
import org.dexenjaeger.algebra.model.spec.GroupSpec;
import org.dexenjaeger.algebra.utils.FunctionsUtil;
import org.dexenjaeger.algebra.validators.ValidationException;
import org.dexenjaeger.algebra.validators.Validator;

import javax.inject.Inject;
import java.util.function.Function;

public class HomomorphismService {
  private final Validator<Group> groupValidator;
  private final Validator<Homomorphism> homomorphismValidator;
  private final GroupService groupService;
  private final FunctionsUtil functionsUtil;
  
  @Inject
  public HomomorphismService(
    Validator<Group> groupValidator,
    Validator<Homomorphism> homomorphismValidator,
    GroupService groupService,
    FunctionsUtil functionsUtil
  ) {
    this.groupValidator = groupValidator;
    this.homomorphismValidator = homomorphismValidator;
    this.groupService = groupService;
    this.functionsUtil = functionsUtil;
  }
  
  private Homomorphism doCreateHomomorphism(
    Group domain, Group range, Group kernel, Function<Integer, Integer> act
  ) {
    Homomorphism result;
    try {
      result = Homomorphism.builder()
                 .domain(domain)
                 .range(range)
                 .kernel(kernel)
                 .mapping(functionsUtil.createMapping(
                   domain.getSize(), act
                 ).getArray())
                 .image(functionsUtil.createImage(
                   domain.getSize(), i -> range.display(act.apply(i))
                 ))
                 .build();
    } catch (NullPointerException e) {
      throw new ValidationException("It is not possible to construct this homomorphism.", e);
    }
    homomorphismValidator.validate(result);
    return result;
  }
  
  public Homomorphism createHomomorphism(
    Group domain, Group range, Group kernel, Function<Integer, Integer> act
  ) {
    groupValidator.validate(domain);
    groupValidator.validate(range);
    groupValidator.validate(kernel);
    return doCreateHomomorphism(domain, range, kernel, act);
  }
  
  public Homomorphism createHomomorphism(
    Group domain,
    Function<Integer, Element> act
  ) {
    groupValidator.validate(domain);
    HomomorphismSummary summary = constructRangeAndKernel(
      domain, act
    );
    SortedGroupResult sortedRange = groupService.createSortedGroup(
      new GroupSpec()
      .setOperatorSymbol(OperatorSymbol.ALTERNATE)
      .setElements(summary.getRangeElementsArray())
      .setOperator(summary::rangeProd)
    );
    SortedGroupResult sortedKernel = groupService.createSortedGroup(
      new GroupSpec()
      .setOperatorSymbol(domain.getOperatorSymbol())
      .setElements(summary.getKernelElementsArray())
      .setOperator(summary::kernelProd)
    );
    
    groupValidator.validate(sortedRange.getGroup());
    groupValidator.validate(sortedKernel.getGroup());
    return doCreateHomomorphism(
      domain,
      sortedRange.getGroup(),
      sortedKernel.getGroup(),
      i -> sortedRange.getRemapper().getReverseLookup().get(act.apply(i))
    );
  }
  
  private HomomorphismSummary constructRangeAndKernel(Group domain, Function<Integer, Element> act) {
    HomomorphismSummary summary = new HomomorphismSummary(domain);
    Element rangeIdentity = act.apply(domain.getIdentity());
    for (int x = 0; x < domain.getSize(); x++) {
      Element y = act.apply(x);
      summary.addRangeValue(y, x);
      if (y.equals(rangeIdentity)) {
        summary.addKernelValue(x);
      }
    }
    return summary;
  }
}
