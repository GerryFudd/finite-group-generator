package org.dexenjaeger.algebra.categories.morphisms;

import org.dexenjaeger.algebra.categories.objects.group.Group;
import org.dexenjaeger.algebra.generators.SymmetryGroupGenerator;
import org.dexenjaeger.algebra.utils.HomomorphismUtil;
import org.dexenjaeger.algebra.validators.ValidationException;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConcreteAutomorphismTest {
  @Test
  void createAutomorphismTest() throws ValidationException {
    Group s3 = SymmetryGroupGenerator.createSymmetryGroup(3);
    Function<String, String> act = a -> {
      if (a.equals("d")) {
        return "d2";
      }
      if (a.equals("d2")) {
        return "d";
      }
      return a;
    };
    
    Homomorphism homomorphism = HomomorphismUtil.createHomomorphism(
      s3, act
    );
    
    Automorphism result = ConcreteAutomorphism.builder()
      .domain(homomorphism.getDomain())
      .range(homomorphism.getRange())
      .act(act)
      .inverseAct(act)
      .build();
    
    assertEquals(
      "\n" +
        "_x____|_I____a____b____c____d____d2___\n" +
        " I    | I    a    b    c    d    d2   \n" +
        " a    | a    I    d    d2   b    c    \n" +
        " b    | b    d2   I    d    c    a    \n" +
        " c    | c    d    d2   I    a    b    \n" +
        " d    | d    c    a    b    d2   I    \n" +
        " d2   | d2   b    c    a    I    d    \n",
      result.getRange().getMultiplicationTable()
    );
  }
  
}