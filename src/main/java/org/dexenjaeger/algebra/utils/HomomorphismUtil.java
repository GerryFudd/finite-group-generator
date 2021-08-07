package org.dexenjaeger.algebra.utils;

import org.dexenjaeger.algebra.categories.objects.Group;
import org.dexenjaeger.algebra.model.OrderedPair;
import org.dexenjaeger.algebra.categories.objects.UnsafeGroup;
import org.dexenjaeger.algebra.categories.objects.UnsafeMonoid;
import org.dexenjaeger.algebra.categories.objects.UnsafeSemigroup;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class HomomorphismUtil {
  public static OrderedPair<Group, Group> constructRangeAndKernel(Group domain, Function<String, String> act) {
    Map<String, String> rangeInversesMap = new HashMap<>();
    Map<String, String> domainLookupMap = new HashMap<>();
    List<String> rangeElements = new LinkedList<>();
    List<String> kernelElements = new LinkedList<>();
  
    String rangeIdentity = act.apply(domain.getIdentity());
    rangeElements.add(rangeIdentity);
    domainLookupMap.put(rangeIdentity, domain.getIdentity());
  
    for (String x:domain.getElementsAsList()) {
      String y = act.apply(x);
      if (!rangeElements.contains(y)) {
        String inverseX = domain.getInverse(x);
        String inverseY = act.apply(inverseX);
        rangeInversesMap.put(y, inverseY);
        domainLookupMap.put(y, x);
        rangeElements.add(y);
        if (!y.equals(inverseY)) {
          rangeInversesMap.put(inverseY, y);
          domainLookupMap.put(inverseY, inverseX);
          rangeElements.add(inverseY);
        }
      }
      if (y.equals(rangeIdentity)) {
        kernelElements.add(x);
      }
    }
  
    return new OrderedPair<>(
      new UnsafeGroup(
        rangeInversesMap,
        new UnsafeMonoid(
          rangeIdentity,
          new UnsafeSemigroup(
            "x",
            rangeElements,
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
  
  public static void validateHomomorphism(
    Group domain,
    Group range,
    Function<String, String> act
  ) {
    for (String a:domain.getElementsAsList()) {
      for (String b:domain.getElementsAsList()) {
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
