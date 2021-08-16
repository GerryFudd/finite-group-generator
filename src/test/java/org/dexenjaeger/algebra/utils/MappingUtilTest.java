package org.dexenjaeger.algebra.utils;

import org.dexenjaeger.algebra.model.Mapping;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MappingUtilTest {
  @Test
  void tryingToMapNonIntTwice() {
    MappingUtil mappingUtil = MappingUtil.init(List.of(
      new Mapping(new int[]{0, 1, 2}),
      new Mapping(new int[]{1, 0, 2}),
      new Mapping(new int[]{2, 1, 0})
    ));
    
    mappingUtil.mapIdentity(new Mapping(new int[]{0, 1, 2}), "E");
    
    mappingUtil.map(new Mapping(new int[]{1, 0, 2}));
    
    RuntimeException e = assertThrows(
      RuntimeException.class,
      () -> mappingUtil.map(new Mapping(new int[]{1, 0, 2}))
    );
    
    assertEquals(
      "Mapping 102 already mapped to a.", e.getMessage()
    );
  }
}