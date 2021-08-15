package org.dexenjaeger.algebra.model.cycle;

import org.dexenjaeger.algebra.utils.Builder;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public abstract class AbstractCycleBuilder<T, U> implements Builder<U> {
  abstract T[] makeEmptyArray();

  protected Map<T, Integer> lookup;

  public AbstractCycleBuilder<T, U> lookup(Map<T, Integer> lookup) {
    this.lookup =lookup;
    return this;
  }

  protected int[] generators;

  public AbstractCycleBuilder<T, U> generators(int[] generators) {
    this.generators =generators;
    return this;
  }

  protected Map<Integer, Integer> subCycleGenerators;

  public AbstractCycleBuilder<T, U> subCycleGenerators(Map<Integer, Integer> subCycleGenerators) {
    this.subCycleGenerators =subCycleGenerators;
    return this;
  }
  protected T[] elements;
  
  public AbstractCycleBuilder<T, U> elements(List<T> elements) {
    this.elements = elements.toArray(makeEmptyArray());
    return this;
  }
  
  protected Function<List<T>, U> maker;
  
  public AbstractCycleBuilder<T, U> maker(Function<List<T>, U> maker) {
    this.maker = maker;
    return this;
  }
}
