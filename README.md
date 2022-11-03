# STARFinder [![Scala Test CI](https://github.com/coco33920/STARFinder/workflows/Scala%20Test%20CI/badge.svg)](https://github.com/coco33920/STARFinder/actions?query=workflow:"Scala+Test+CI")

<div align="center">

[![Language - Scala](https://img.shields.io/badge/Language-Scala-darkred?style=for-the-badge&logo=scala)](https://www.scala-lang.org/) 
[![Licence](https://img.shields.io/badge/License-GPLv3-red?style=for-the-badge&logo=GNU&color=55cdfc)](LICENCE.md)
[![Tag](https://img.shields.io/github/v/release/coco33920/STARFinder.svg?include_prereleases=&sort=semver&color=f7a8d8&style=for-the-badge&logo=github)](https://github.com/coco33920/STARFinder/releases/latest)

</div>

<hr>

*STARFinder* is a little project to learn Scala, it's built to learn to use features 
from *Scala*, coming from *OCaml*. The Goal of STARFinder is to use a little 
easy language to find bus stop according to rules (ex: all bus stops with the C1 or the C2 and the 
underground b is `(C1 or C2) and b`).

## Usage
You can download the latest `star-finder.jar` in the [`releases`](https://github.com/coco33920/STARFinder/releases/latest) tab of github or you can compile the code (see [compile](#compiling))

You can just launch the REPL by running the jar (note: the main code was compiled with OpenJDK 19), switching the providers 
by using the `--provider "provider"` argument, to see the whole list just run `--help` as arguments

To use the language, see [examples](#examples) or the [language specification](#the-language)

## Current Providers
The current supported backends are
* STAR (Rennes)
* TAN (Nantes)

## Run
You can run the project with `sbt`
```bash
git clone git@github.com:coco33920/STARFinder.git
cd STARFinder/
sbt "run <arguments>"
```

## Compiling
To compile the program to a jar just run
```bash
git clone git@github.com:coco33920/STARFinder.git
cd STARFinder/
sbt assembly
java -jar target/scala-3.2.0/star-finder.jar <arguments>
```

## The Language
### Finding bus stops
The main part of the Scala part of the project is to lex and parse a little 
language to construct the commands, it supports unicode for multiple way to create the 
command for example
* `and`
* `&`
* `-`
* `∩`

all lex to the `AND` operator,

* `or`
* `|`
* `∪`
* `+`

all lex to the `OR` operator and finally 

* `not`
* `¬`

lex to the `NOT` operator so the commands

* `(C1 or C2) and b`
* `(C1 | C2) & b`
* etc.

all lex to the same token list, and are all parse to the same commands,
and naturally you can mix and match if you want for example 

* `(C1 | C2) ∩ b`
* `(C1 or C2) & b`
* etc.

### Keywords
The language have three keywords, two uses int as parameters
* `allow <int>` ([see](#finding-paths-between-stops)) specify to the interpreter how many "hops" or 
intermediary stops you're keen to take to find a path between two stops (warning, a huge number means a `lot` of possible connections)
you can also use `@ <int>` which is the same keyword
* `show <int>` is used after a logical command ([see](#finding-bus-stops)) like `C1 or C2 show 5` to limit the number of lines printed to `5` 
you can also use `limit <int>` or `<< <int>` which are the same keyword

The last one take a string as parameter
* `using <str>` takes a string as a list of lines, like `using C1,a` and limits the starting lines for the `to` operators

All keywords can be used in whatever order you like it doesn't matter 

### Finding paths between stops
You can find paths between two stops with the `to` operator, which can be written `->` or `→`, and the
`allow` keywords specify how many hops you are keen to take, by default it is `0̀` (so a direct connection)
example of usage

* `Républiques → Gares`
* `Gares → Tournebride allow 1`

## Examples
### Help menu
![help](https://user-images.githubusercontent.com/17108449/199769299-d8fcea1d-82ff-4e49-b9e9-1a861a3e4f20.png)

### All STAR stops with the C1 and the metro (a or b)
![image](https://user-images.githubusercontent.com/17108449/199769707-e6e8c4bd-b3cc-4853-be18-c89571ae688c.png)

### Direct connections between Gares and République
![image](https://user-images.githubusercontent.com/17108449/199769989-658a1e3c-6f62-4e4b-92e6-838cdcee751a.png)

### Itineraries between Gare and Tournebride using only the *a* from Gares
![image](https://user-images.githubusercontent.com/17108449/199770543-a10c7d08-9544-4be9-a36c-6df9c7c103a4.png)

And many more, enjoy! :)


## The Providers (not implemented  yet)
Java is used to provide an API in the form of an interface, that can 
be implemented to provide support for multiple backends (STAR, TBM, etc.)

## TODO List
- [X] AST Type
- [X] Parser *(Scala)*
  - [X] Basic Parser
  - [X] Applying the *not* operator for all expressions
- [X] Printing AST
- [X] API *(Java)*
  - [X] Implementation of the database scheme for STAR
  - [X] Implementation from the interpreter point of vue
  - [X] Fully functioning one implementation (STAR)
- [X] Tests *(Scala)*
- [X] Interpreter (*Translator to SQL*) *(Scala)*
- [X] Command line launch *(Scala)*
- [X] Cache *(Java)*
- [X] Better REPL
- [X] Update provider
- [ ] Implementing more providers 
  - [X] Rennes (STAR)
  - [ ] Paris
  - [ ] Lyon
  - [X] Nantes (TAN)
  - [ ] Marseille
  - [ ] Lille
  - [ ] Bordeaux
  - [ ] Toulouse 
  - [ ] Montpellier

## Libraries
This project is using
* [Scala-Test](https://github.com/scalatest/scalatest) under Apache-2.0 license
* [Decline](https://github.com/bkirwi/decline) under Apache-2.0 license
* [µPickles](https://github.com/com-lihaoyi/upickle) under MIT license
* [os](https://github.com/com-lihaoyi/os-lib) under MIT license
* [sql-lite jdbc](https://github.com/xerial/sqlite-jdbc) under Apache-2.0 and BSD license 
* [apache common-io](https://github.com/apache/commons-io) under Apache-2.0 license
* [JLine](https://github.com/jline/jline3) under BSD license (see [JLine disclaimer](LICENCE-jline.md))
* [org.Json](https://github.com/stleary/JSON-java) in the public domain
