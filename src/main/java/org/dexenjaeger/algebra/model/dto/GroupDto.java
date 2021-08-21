package org.dexenjaeger.algebra.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class GroupDto {
  private String operatorSymbol;
  private List<CycleDto> maximalCycles;
  private List<List<Integer>> multiplicationTable;
}
