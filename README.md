# Finite Group Generator

This app is intended to calculate finite groups from permutation sets. It has methods that calculate some common types of groups that are studied in an Abstract Algebra course.

- Symmetry Groups: groups formed by the set of all permutations of a finite set
- Cyclic Groups: groups that are formed by repeatedly multiplying a value by itself
- All Automorphisms of a known group: the invertible functions from the group to itself that preserve the group operation (f(ab)=f(a)f(b)).
- The Inner Automorphisms of a known group: The Automorphisms formed by conjugating by a particular element (g -> a^-1ga).

## Next steps

There are only tests of the various group generators, but no way to interact with a deployed version of the code. I am planning to set up methods that can export results in csv or json format.
