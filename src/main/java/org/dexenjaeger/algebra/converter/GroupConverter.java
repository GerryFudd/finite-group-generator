package org.dexenjaeger.algebra.converter;

import org.dexenjaeger.algebra.categories.objects.group.Group;
import org.dexenjaeger.algebra.model.Element;
import org.dexenjaeger.algebra.model.cycle.Cycle;
import org.dexenjaeger.algebra.model.dto.CycleDto;
import org.dexenjaeger.algebra.model.dto.GroupDto;
import org.dexenjaeger.algebra.utils.io.latex.GroupAsLatex;
import org.dexenjaeger.algebra.utils.io.latex.LatexCellSpec;

import java.util.Comparator;
import java.util.stream.Collectors;

public class GroupConverter {
  public static GroupDto toDto(Group group) {
    return new GroupDto()
             .setOperatorSymbol(group.getOperatorSymbol().getAscii())
             .setMultiplicationTable(group.getMultiplicationTable())
             .setMaximalCycles(
               group.getMaximalCycles()
                 .stream()
                 .sorted(Comparator.comparing(Cycle::getSize))
                 .map(cycle -> new CycleDto()
                                 .setSize(cycle.getSize())
                                 .setGenerator(cycle.get(0))
                                 .setGeneratorSymbol(
                                   group.display(cycle.get(0))
                                     .getAscii()
                                 ))
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
               .map(Element::getLatex)
               .map(LatexCellSpec::new)
               .toArray(LatexCellSpec[]::new)
             )
      .setMultiplicationTable(group.getMultiplicationTable());
  }
}
