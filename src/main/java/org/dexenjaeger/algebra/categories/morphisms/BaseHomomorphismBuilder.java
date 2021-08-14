package org.dexenjaeger.algebra.categories.morphisms;

import org.dexenjaeger.algebra.categories.objects.group.Group;
import org.dexenjaeger.algebra.model.OrderedPair;
import org.dexenjaeger.algebra.utils.Builder;

import java.util.LinkedList;
import java.util.Objects;
import java.util.function.Function;

public abstract class BaseHomomorphismBuilder<T> implements Builder<T> {
  protected Group domain;
  
  public BaseHomomorphismBuilder<T> domain(Group domain) {
    this.domain = domain;
    return this;
  }
  
  protected Group range;
  
  public BaseHomomorphismBuilder<T> range(Group range) {
    this.range = range;
    return this;
  }
  
  protected Group kernel;
  
  public BaseHomomorphismBuilder<T> kernel(Group kernel) {
    this.kernel = kernel;
    return this;
  }
  
  protected Function<Integer, Integer> act;
  
  public BaseHomomorphismBuilder<T> act(Function<Integer, Integer> act) {
    this.act = act;
    return this;
  }
  
  protected int[] mapping;
  
  public BaseHomomorphismBuilder<T> mapping(int[] mapping) {
    this.mapping = mapping;
    return this;
  }
  
  protected String[] image;
  
  public BaseHomomorphismBuilder<T> image(String[] image) {
    this.image = image;
    return this;
  }
  
  protected Function<Integer, String> imageFunc;
  
  public BaseHomomorphismBuilder<T> imageFunc(Function<Integer, String> imageFunc) {
    this.imageFunc = imageFunc;
    return this;
  }
  
  protected OrderedPair<int[], String[]> resolveMapping() {
    LinkedList<String> imageList = new LinkedList<>();
    if (mapping != null) {
      if (image != null) {
        return new OrderedPair<>(mapping, image);
      }
      while (imageList.size() < mapping.length) {
        if (imageFunc == null) {
          imageList.addLast(range.display(mapping[imageList.size()]));
        } else {
          imageList.addLast(imageFunc.apply(imageList.size()));
        }
      }
      return new OrderedPair<>(
        mapping, imageList.toArray(new String[0])
      );
    }
    
    mapping = new int[domain.getSize()];
    
    for (int i = 0; i < domain.getSize(); i++) {
      mapping[i] = act.apply(i);
      if (image == null) {
        if (imageFunc == null) {
          imageList.addLast(range.display(mapping[i]));
        } else {
          imageList.addLast(imageFunc.apply(i));
        }
      }
    }
    return new OrderedPair<>(mapping, Objects.requireNonNullElseGet(image, () -> imageList.toArray(new String[0])));
  
  }
}
