# Finite Group Generator

## About

This app is intended to calculate finite groups from permutation sets. It has methods that calculate some common types of groups that are studied in an Abstract Algebra course.

- Symmetry Groups: groups formed by the set of all permutations of a finite set
- Cyclic Groups: groups that are formed by repeatedly multiplying a value by itself
- All Automorphisms of a known group: the invertible functions from the group to itself that preserve the group operation (f(ab)=f(a)f(b)).
- The Inner Automorphisms of a known group: The Automorphisms formed by conjugating by a particular element (g -> a^-1ga).

## Setup

Use gradle to install the project dependencies.

## Usage

For now, you can create an app.properties file at the root of the project (copy the app.properties.sample) file, set input and output directories, and set a source symmetry group "spec" file. This must be a JSON file that contains an array of SymmetryGroupGeneratorSpec objects. If you run the Main.main method, these symmetry groups will be written to the output directory according to your specifications.


For example, the following will result in a LaTeX table written to the file sym4.tex that contains the multiplication table for S_4 and using the `$\circ$` symbol as its operator symbol.

```json
[
  {
    "elementsCount": 4,
    "operatorSymbol": "COMPOSITION",
    "fileName": "sym4",
    "fileType": "LATEX"
  }
]
```

## Next steps

### Usability

I would also like to make this into an exportable jar file so that people could import this library into their own projects.

### Performance

I have brute-forced the computation of Automorphisms because I wanted to see how much I could optimize this computation before looking up the best approach (this was originally a learning exercise). I now plan to find some more theory about the computation of automorphism groups and apply those to see how much I can optimize this computation.

### Additional features

I plan to add methods that will compute the subgroups and normal subgroups of provided finite groups.
