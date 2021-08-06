package org.dexenjaeger.algebra.model;

public interface AlgebraicStructureWithInverses extends AlgebraicStructureWithIdentity{
  String getInverse(String element);
}
