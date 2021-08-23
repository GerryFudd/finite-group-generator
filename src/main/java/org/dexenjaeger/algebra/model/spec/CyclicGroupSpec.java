package org.dexenjaeger.algebra.model.spec;

import lombok.Data;
import org.dexenjaeger.algebra.model.binaryoperator.Element;
import org.dexenjaeger.algebra.model.binaryoperator.OperatorSymbol;

@Data
public class CyclicGroupSpec {
  private OperatorSymbol operatorSymbol = OperatorSymbol.DEFAULT;
  private String base;
  private Element identityElement = Element.I;
  private int n = 1;
}
