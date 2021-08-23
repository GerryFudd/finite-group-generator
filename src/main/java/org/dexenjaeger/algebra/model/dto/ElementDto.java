package org.dexenjaeger.algebra.model.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ElementDto {
  private final boolean addition;
  
  @JsonCreator
  public ElementDto(@JsonProperty("addition") boolean addition) {
    this.addition = addition;
  }
  
  private String base;
  private Integer pow;
}
