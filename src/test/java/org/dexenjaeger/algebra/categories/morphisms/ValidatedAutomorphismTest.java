package org.dexenjaeger.algebra.categories.morphisms;

import org.dexenjaeger.algebra.categories.objects.SafeGroup;
import org.dexenjaeger.algebra.categories.objects.ValidatedGroup;
import org.dexenjaeger.algebra.generators.SymmetryGroupGenerator;
import org.dexenjaeger.algebra.model.BinaryOperatorSummary;
import org.dexenjaeger.algebra.model.ValidatedGroupSpec;
import org.dexenjaeger.algebra.utils.BinaryOperatorUtil;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ValidatedAutomorphismTest {
  @Test
  void createAutomorphismTest() {
    SafeGroup s3 = SymmetryGroupGenerator.createSymmetryGroup(3);
    Function<String, String> act = a -> {
      if (a.equals("d")) {
        return "d2";
      }
      if (a.equals("d2")) {
        return "d";
      }
      return a;
    };
    
    Automorphism result = ValidatedAutomorphism.createAutomorphism(
      ValidatedHomomorphism.createHomomorphism(
        s3, act
      ), act
    );
    
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