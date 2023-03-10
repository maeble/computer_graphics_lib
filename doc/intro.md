# Introduction to comp-graphics-lib

- [Introduction to comp-graphics-lib](#introduction-to-comp-graphics-lib)
  - [Content(s) and algorithm(s)](#contents-and-algorithms)
    - [Texture Generation](#texture-generation)
    - [Color Generation](#color-generation)
  - [Comp-graphics-lib as a clojure playground](#comp-graphics-lib-as-a-clojure-playground)
  - [Project base "features"](#project-base-features)


## Content(s) and algorithm(s)

### Texture Generation

The `texture_generation` package of the `computer graphics lib` contains a demonstration of the forest fire texture generation algorithm.

The **forest fire algorithm** illustrates the spread of a forest fire. Each cell can be either forest, empty or burning. Through various transformation rules, the state of each cell is changed once per iteration on a random basis and influenced by surrounding cells. Each cell and its state is visualized with a pixel and its color. This way, the algorithm can be used for texture generation.

Transformation rules:
- *empty cells* can grow to a forest by a certain probability
- *forest cells* can catch fire by a certain probability. The more surrounding cells are on fire, the more likely the cell will be to catch fire, but there is also a small base probability.
- *fire cells* always burn down to a barren cell
 
The `-main` function in the `comp_graphics_lib.core` file runs a visual demonstration of the implemented forest fire algorithm in a simple GUI.


### Color Generation

The `texture_generation` package of the `computer graphics lib` contains an algorithm to transform text into a colorful texture map.

A GUI can be opened via a REPL, but it is not integrated in the core UI in `core.clj` and is non-interactive. It visualizes the generated texture.


## Comp-graphics-lib as a clojure playground

The code was primarily written as an experimental playground for functional programming with Clojure.

Hereby, the following topics are covered:
- recursion
- reduction
- java interoperability
- currying (partial)
- sequential and associative destructuring
- pattern matching
- lambda
- macros
- composition

## Project base "features"

- the *comments* point out the usage of the clojure topics listed above and explain implementation decisions
- *inline documentation* explains the behaviour of all important functions
- *tests* verify the correct behaviour of the functions
