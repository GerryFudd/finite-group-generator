package org.dexenjaeger.algebra.service;

import org.dexenjaeger.algebra.categories.morphisms.Homomorphism;
import org.dexenjaeger.algebra.categories.objects.group.Group;
import org.dexenjaeger.algebra.model.HomomorphismSummary;
import org.dexenjaeger.algebra.model.SortedGroupResult;
import org.dexenjaeger.algebra.model.cycle.IntCycle;
import org.dexenjaeger.algebra.utils.BinaryOperatorUtil;
import org.dexenjaeger.algebra.utils.FunctionsUtil;
import org.dexenjaeger.algebra.validators.ValidationException;
import org.dexenjaeger.algebra.validators.Validator;

import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

public class HomomorphismService {
  private final Validator<Group> groupValidator;
  private final Validator<Homomorphism> homomorphismValidator;
  private final GroupService groupService;
  private final BinaryOperatorUtil binaryOperatorUtil;
  private final FunctionsUtil functionsUtil;
  
  @Inject
  public HomomorphismService(
    Validator<Group> groupValidator,
    Validator<Homomorphism> homomorphismValidator,
    GroupService groupService,
    BinaryOperatorUtil binaryOperatorUtil,
    FunctionsUtil functionsUtil
  ) {
    this.groupValidator = groupValidator;
    this.homomorphismValidator = homomorphismValidator;
    this.groupService = groupService;
    this.binaryOperatorUtil = binaryOperatorUtil;
    this.functionsUtil = functionsUtil;
  }
  
  private Homomorphism doCreateHomomorphism(
    Group domain, Group range, Group kernel, Function<Integer, Integer> act
  ) throws ValidationException {
    Homomorphism result;
    try {
      result = Homomorphism.builder()
                 .domain(domain)
                 .range(range)
                 .kernel(kernel)
                 .mapping(functionsUtil.createMapping(
                   domain.getSize(), act
                 ))
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
  ) throws ValidationException {
    groupValidator.validate(domain);
    groupValidator.validate(range);
    groupValidator.validate(kernel);
    return doCreateHomomorphism(domain, range, kernel, act);
  }
  
  public Homomorphism createHomomorphism(
    Group domain,
    Function<Integer, String> act
  ) throws ValidationException {
    groupValidator.validate(domain);
    HomomorphismSummary summary = constructRangeAndKernel(
      domain, act
    );
    SortedGroupResult sortedRange = groupService.createSortedGroup(
      "x",
      summary.getRangeElementsArray(),
      summary.getRangeInversesMap(),
      summary.getRangeMaximalCycles(),
      summary::rangeProd
    );
    SortedGroupResult sortedKernel = groupService.createSortedGroup(
      domain.getOperatorSymbol(),
      summary.getKernelElementsArray(),
      summary.getKernelInversesMap(),
      summary.getKernelMaximalCycles(),
      summary::kernelProd
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
  
  private HomomorphismSummary constructRangeAndKernel(Group domain, Function<Integer, String> act) {
    HomomorphismSummary summary = new HomomorphismSummary(domain);
    
    String rangeIdentityDisplay = act.apply(domain.getIdentity());
    summary.setRangeIdentity(
      rangeIdentityDisplay, domain.getIdentity()
    );
    
    for (IntCycle cycle:domain.getMaximalCycles()) {
      LinkedList<String> rangeCycle = new LinkedList<>();
      List<String> kernelCycle = new LinkedList<>();
      LinkedList<Integer> domainCycle = new LinkedList<>(cycle.getElements());
      
      while (!domainCycle.isEmpty() && (
        rangeCycle.isEmpty() || !rangeCycle.getLast().equals(rangeIdentityDisplay)
      )) {
        int x = domainCycle.removeFirst();
        rangeCycle.addLast(act.apply(x));
        if (rangeCycle.getLast().equals(rangeIdentityDisplay)) {
          kernelCycle.add(domain.display(x));
        }
      }
      if (rangeCycle.size() > 1) {
        summary.addRangeMaximalCycle(
          rangeCycle, cycle.get(0)
        );
      }
      if (kernelCycle.size() > 0 &&
            !kernelCycle.get(0).equals(domain.getIdentityDisplay())) {
        String kernGen = kernelCycle.get(0);
        String kernNext = domain.prod(kernGen, kernGen);
        while (!kernNext.equals(kernGen)) {
          kernelCycle.add(kernNext);
          kernNext = domain.prod(kernGen, kernNext);
        }
        if (kernelCycle.size() > 1) {
          summary.addKernelCycle(kernelCycle);
        }
      }
    }
    return summary;
  }
}
