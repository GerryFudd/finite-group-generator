package org.dexenjaeger.algebra.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.dexenjaeger.algebra.categories.objects.group.Group;
import org.dexenjaeger.algebra.utils.Remapper;

@Getter
@RequiredArgsConstructor
public class SortedGroupResult {
  private final Group group;
  private final Remapper remapper;
}
