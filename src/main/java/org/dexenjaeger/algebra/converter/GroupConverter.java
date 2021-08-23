package org.dexenjaeger.algebra.converter;

import org.dexenjaeger.algebra.categories.objects.group.Group;
import org.dexenjaeger.algebra.model.binaryoperator.OperatorSymbol;
import org.dexenjaeger.algebra.model.dto.ElementDto;
import org.dexenjaeger.algebra.model.dto.GroupDto;
import org.dexenjaeger.algebra.utils.io.latex.GroupAsLatex;
import org.dexenjaeger.algebra.utils.io.latex.LatexCellSpec;

import java.util.stream.Collectors;

public class GroupConverter {
  public static GroupDto toDto(Group group) {
    return new GroupDto()
             .setOperatorSymbol(group.getOperatorSymbol().getJson())
             .setMultiplicationTable(group.getMultiplicationTable())
             .setElements(
               group.getSortedElements()
                 .stream()
                 .map(el -> new ElementDto(
                   group.getOperatorSymbol()
                     == OperatorSymbol.ADDITION
                 )
                                 .setPow(el.getPow())
                                 .setBase(el.getBase()))
                 .collect(Collectors.toList())
             );
  }
  
  public static GroupAsLatex toLatex(Group group) {
    return new GroupAsLatex()
      .setOperatorAsLatex(
        new LatexCellSpec(group.getOperatorSymbol().getLatex())
      )
             .setLatexElements(
               group.getSortedElements().stream()
               .map(el -> el.getLatex(
                 group.getOperatorSymbol()
               ))
               .map(LatexCellSpec::new)
               .toArray(LatexCellSpec[]::new)
             )
      .setMultiplicationTable(group.getMultiplicationTable());
  }
}
