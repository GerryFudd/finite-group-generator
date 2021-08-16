package org.dexenjaeger.algebra.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.dexenjaeger.algebra.model.Mapping;
import org.dexenjaeger.algebra.model.cycle.MappingCycle;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public class MappingUtil {
  private final SymbolsUtil symbolsUtil;
  private final Set<Mapping> unmapped;
  @Getter
  private Mapping identity;
  @Getter
  private final LinkedList<Mapping> mapped = new LinkedList<>();
  
  public static MappingUtil init(List<Mapping> mappings) {
    return new MappingUtil(
      new SymbolsUtil(),
      new HashSet<>(mappings)
    );
  }
  
  public boolean missingIdentity() {
    return identity == null;
  }
  
  public void mapIdentity(Mapping mapping, String symbol) {
    map(mapping, symbol);
    this.identity = mapping;
  }
  
  public void map(Mapping mapping) {
    map(mapping, null);
  }
  
  public void map(Mapping mapping, String symbol) {
    if (mapping.equals(identity)) {
      return;
    }
    // TODO: test this
    if (!unmapped.remove(mapping)) {
      Mapping mappedVersion = mapped.get(mapped.indexOf(mapping));
      throw new MappingException(String.format(
        "Mapping %s already mapped to %s.",
        mapping.arrayString(), mappedVersion
      ));
    }
    if (symbol == null) {
      mapped.addLast(symbolsUtil.applySymbol(mapping));
    } else {
      mapped.addLast(symbolsUtil.applySymbol(mapping, symbol));
    }
  }
  
  public void mapRemaining() {
    while (!unmapped.isEmpty()) {
      map(unmapped.stream().findAny().orElseThrow());
    }
  }
  
  public void mapCycle(MappingCycle cycle) {
    String cycleSymbol = symbolsUtil.getNextSymbol();
    cycle.getElements().forEach(el -> map(el
    , cycleSymbol));
  }
}
