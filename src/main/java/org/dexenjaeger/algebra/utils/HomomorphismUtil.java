package org.dexenjaeger.algebra.utils;

import org.dexenjaeger.algebra.categories.objects.group.Group;
import org.dexenjaeger.algebra.categories.objects.group.SafeGroup;
import org.dexenjaeger.algebra.categories.objects.group.UnsafeGroup;
import org.dexenjaeger.algebra.categories.objects.monoid.UnsafeMonoid;
import org.dexenjaeger.algebra.categories.objects.semigroup.UnsafeSemigroup;
import org.dexenjaeger.algebra.model.OrderedPair;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class HomomorphismUtil {
  private static RuntimeException getInvalidCycleException(List<String> cycle) {
    return new RuntimeException(String.format(
      "Cycle is not valid: %s", String.join(", ", cycle)
    ));
  }
  public static OrderedPair<Group, Group> constructRangeAndKernel(SafeGroup domain, Function<String, String> act) {
    Map<String, String> rangeInversesMap = new HashMap<>();
    Map<String, String> domainLookupMap = new HashMap<>();
    List<String> rangeElements = new LinkedList<>();
    Map<Integer, Set<List<String>>> rangeCycles = new HashMap<>();
    List<String> kernelElements = new LinkedList<>();
    Map<Integer, Set<List<String>>> kernelCycles = new HashMap<>();
  
    String rangeIdentity = act.apply(domain.getIdentity());
    
    rangeElements.add(rangeIdentity);
    kernelElements.add(domain.getIdentity());
    domainLookupMap.put(rangeIdentity, domain.getIdentity());
    rangeCycles.put(1, Set.of(List.of(rangeIdentity)));
    kernelCycles.put(1, Set.of(List.of(domain.getIdentity())));
  
    for (Integer n:domain.getCycleSizes()) {
      for (List<String> cycle:domain.getNCycles(n)) {
        LinkedList<String> linkedCycle = new LinkedList<>(cycle);
        LinkedList<String> linkedRangeCycle = new LinkedList<>();
        LinkedList<String> linkedRangeInverses = new LinkedList<>();
        LinkedList<String> linkedKernelCycle = new LinkedList<>();
        LinkedList<String> linkedKernelInverses = new LinkedList<>();
        String cycleId = linkedCycle.removeLast();
        if (!cycleId.equals(domain.getIdentity())) {
          throw getInvalidCycleException(cycle);
        }
        while (!linkedCycle.isEmpty()) {
          String x = linkedCycle.removeFirst();
          String y = act.apply(x);
          if (!rangeElements.contains(y)) {
            domainLookupMap.put(y, x);
            rangeElements.add(y);
            linkedRangeCycle.addLast(y);
  
            if (!linkedCycle.isEmpty()) {
              String inverseX = linkedCycle.getLast();
              if (!inverseX.equals(domain.getInverse(x))) {
                throw getInvalidCycleException(cycle);
              }
              String inverseY = act.apply(inverseX);
              rangeInversesMap.put(inverseY, y);
              domainLookupMap.put(inverseY, inverseX);
              rangeElements.add(inverseY);
              linkedRangeInverses.addFirst(inverseY);
            } else {
              rangeInversesMap.put(y, y);
            }
          } else if (y.equals(rangeIdentity)) {
            kernelElements.add(x);
            linkedKernelCycle.addLast(x);
            if (!linkedCycle.isEmpty()) {
              String inverseX = linkedCycle.removeLast();
              if (!inverseX.equals(domain.getInverse(x))) {
                throw getInvalidCycleException(cycle);
              }
              if (!act.apply(inverseX).equals(rangeIdentity)) {
                throw new RuntimeException(String.format(
                  "Invalid homomorphism. Kernel is not a subgroup since %s is in the kernel but its inverse %s is not.", x, inverseX
                ));
              }
              linkedKernelInverses.addFirst(inverseX);
              kernelElements.add(inverseX);
            }
          } else if (!linkedCycle.isEmpty()){
            linkedCycle.removeLast();
          }
        }
        if (!linkedRangeCycle.isEmpty()) {
          while (!linkedRangeInverses.isEmpty()) {
            linkedRangeCycle.addLast(linkedRangeInverses.removeFirst());
          }
          linkedRangeCycle.addLast(rangeIdentity);
          rangeCycles.compute(linkedRangeCycle.size(), (key, cycles) -> {
            if (cycles == null) {
              cycles = new HashSet<>();
            }
            cycles.add(linkedRangeCycle);
            return cycles;
          });
        }
        if (!linkedKernelCycle.isEmpty()) {
          while (!linkedKernelInverses.isEmpty()) {
            linkedKernelCycle.addLast(linkedKernelInverses.removeFirst());
          }
          linkedKernelCycle.addLast(domain.getIdentity());
          kernelCycles.compute(linkedKernelCycle.size(), (key, cycles) -> {
            if (cycles == null) {
              cycles = new HashSet<>();
            }
            cycles.add(linkedKernelCycle);
            return cycles;
          });
        }
      }
    }
  
    return new OrderedPair<>(
      new UnsafeGroup(
        rangeInversesMap,
        rangeCycles,
        new UnsafeMonoid(
          rangeIdentity,
          new UnsafeSemigroup(
            "x",
            rangeElements.stream().sorted().collect(Collectors.toList()),
            (a, b) -> act.apply(domain.getProduct(
              domainLookupMap.get(a),
              domainLookupMap.get(b)
            ))
          )
        )
      ),
      new UnsafeGroup(
        kernelElements.stream().collect(Collectors.toMap(
          Function.identity(),
          domain::getInverse
        )),
        kernelCycles,
        new UnsafeMonoid(
          domain.getIdentity(),
          new UnsafeSemigroup(
            domain.getOperatorSymbol(),
            kernelElements,
            domain::getProduct
          )
        )
      )
    );
  }
  
  private static RuntimeException getNotFunctionException(List<String> rangeElements, String output, String input) {
    return new RuntimeException(String.format(
      "Range %s doesn't contain image %s of %s.",
      String.join(", ", rangeElements),
      output, input
    ));
  }
  
  public static void validateHomomorphism(
    Group domain,
    Group range,
    Function<String, String> act
  ) {
    List<String> rangeElements = range.getElementsAsList();
    for (String a:domain.getElementsAsList()) {
      String fa = act.apply(a);
      if (!rangeElements.contains(fa)) {
        throw getNotFunctionException(rangeElements, fa, a);
      }
      for (String b:domain.getElementsAsList()) {
        String fb = act.apply(b);
        if (!rangeElements.contains(fb)) {
          throw getNotFunctionException(rangeElements, fb, b);
        }
        if (!rangeElements.contains(range.getProduct(fa, fb))) {
          throw new RuntimeException(String.format(
            "Range %s isn't closed under %s.",
            String.join(", ", rangeElements), range.getOperatorSymbol()
          ));
        }
        if (!range.getProduct(act.apply(a), act.apply(b))
               .equals(act.apply(domain.getProduct(a, b)))) {
          throw new RuntimeException("Function is not a homomorphism.");
        }
      }
    }
  }
  
  public static <T,U> void validateBijection(
    Collection<T> domain, Collection<U> range, Function<T, U> func
  ) {
    RuntimeException e = new RuntimeException("Function is not a bijection");
    if (domain.size() != range.size()) {
      throw e;
    }
    Set<U> image = new HashSet<>();
    for (T x:domain) {
      U y = func.apply(x);
      if (!range.contains(y) || !image.add(y)) {
        throw e;
      }
    }
  }
  
  public static <T,U> void validateLeftInverse(
    Collection<T> domain, Function<T, U> func, Function<U, T> inverse
  ) {
    for (T x:domain) {
      if (!x.equals(inverse.apply(func.apply(x)))) {
        throw new RuntimeException("Function is not a left inverse.");
      }
    }
  }
  
  public static void validateInverseImageOfId(Group domain, Group kernel, String identity, Function<String, String> act) {
    for (String a:domain.getElementsAsList()) {
      if (kernel.getElementsAsList().contains(a) != act.apply(a).equals(identity)) {
        throw new RuntimeException("Subset is not the inverse image of the identity.");
      }
    }
  }
}
