package org.dexenjaeger.algebra.utils;

import org.dexenjaeger.algebra.categories.objects.Group;
import org.dexenjaeger.algebra.model.OrderedPair;
import org.dexenjaeger.algebra.categories.objects.UnsafeGroup;
import org.dexenjaeger.algebra.categories.objects.UnsafeMonoid;
import org.dexenjaeger.algebra.categories.objects.UnsafeSemigroup;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
        rangeInversesMap.put(inverseY, y);
        domainLookupMap.put(y, x);
        domainLookupMap.put(inverseY, inverseX);
        rangeElements.add(y);
        rangeElements.add(inverseY);
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
  
  public static boolean isHomomorphism(
    Group domain,
    Group range,
    Function<String, String> act
  ) {
    for (String a:domain.getElementsAsList()) {
      for (String b:domain.getElementsAsList()) {
        if (!range.getProduct(act.apply(a), act.apply(b))
               .equals(act.apply(domain.getProduct(a, b)))) {
          return false;
        }
      }
    }
    return true;
  }
  
  public static boolean isInverseImageOfId(Group domain, Group kernel, String identity, Function<String, String> act) {
    for (String a:domain.getElementsAsList()) {
      if (kernel.getElementsAsList().contains(a) != act.apply(a).equals(identity)) {
        return false;
      }
    }
    return true;
  }
}
