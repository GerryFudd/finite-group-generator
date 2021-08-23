package org.dexenjaeger.algebra.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class GroupDto {
  private String operatorSymbol;
  private List<ElementDto> elements;
  private int[][] multiplicationTable;
}
