package org.dexenjaeger.algebra.service;

import org.dexenjaeger.algebra.categories.morphisms.Homomorphism;
import org.dexenjaeger.algebra.categories.objects.group.Group;
import org.dexenjaeger.algebra.model.HomomorphismSummary;
import org.dexenjaeger.algebra.model.cycle.IntCycle;
import org.dexenjaeger.algebra.validators.ValidationException;
import org.dexenjaeger.algebra.validators.Validator;

import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

public class HomomorphismService {
  private final Validator<Group> groupValidator;
  private final Validator<Homomorphism> homomorphismValidator;
  
  @Inject
  public HomomorphismService(
    Validator<Group> groupValidator,
    Validator<Homomorphism> homomorphismValidator
  ) {
    this.groupValidator = groupValidator;
    this.homomorphismValidator = homomorphismValidator;
  }
  
  private Homomorphism doCreateHomomorphism(
    Group domain, Group range, Group kernel, Function<Integer, Integer> act
  ) throws ValidationException {
    return doCreateHomomorphism(domain, range, kernel, act, null);
  }
  
  private Homomorphism doCreateHomomorphism(
    Group domain, Group range, Group kernel, Function<Integer, Integer> act, Function<Integer, String> imageFunc
  ) throws ValidationException {
    Homomorphism result;
    try {
      result = Homomorphism.builder()
                 .domain(domain)
                 .range(range)
                 .kernel(kernel)
                 .act(act)
                 .imageFunc(imageFunc)
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
    
    Group range = summary.getRange();
    Group kernel = summary.getKernel();
    
    groupValidator.validate(range);
    groupValidator.validate(kernel);
    return doCreateHomomorphism(
      domain, range, kernel, summary.getAct(), act
    );
  }
  
  private HomomorphismSummary constructRangeAndKernel(Group domain, Function<Integer, String> act) {
    HomomorphismSummary summary = new HomomorphismSummary(domain, act);
    
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
