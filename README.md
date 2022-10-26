# STARFinder [![Scala Test CI](https://github.com/coco33920/STARFinder/workflows/Scala%20Test%20CI/badge.svg)](https://github.com/coco33920/STARFinder/actions?query=workflow:"Scala+Test+CI")

<div align="center">

[![License - GPLv3](https://img.shields.io/badge/License-GPLv3-55cdfc?style=for-the-badge&logo=GNU)](https://opensource.org/licenses/GPL-3.0) [![Language - Scala](https://img.shields.io/badge/Language-Scala-red?style=for-the-badge&logo=scala)](https://www.scala-lang.org/) [![Tag](https://img.shields.io/github/v/release/coco33920/STARFinder.svg?include_prereleases=&sort=semver&color=f7a8d8&style=for-the-badge&logo=github)](https://github.com/coco33920/STARFinder/releases)

</div>

<hr>

*STARFinder* is a little project to learn Scala, it's built to learn to use features 
from *Scala*, coming from *OCaml*. The Goal of STARFinder is to use a little 
easy language to find bus stop according to rules (ex: all bus stops with the C1 or the C2 and the 
underground b is `(C1 or C2) and b`).

## Usage
You can download the latest jar in the [`releases`](https://github.com/coco33920/STARFinder/releases) tab of github or you can compile the code (see [compile](#compiling))

You can just launch the REPL by running the jar (note: the main code was compiled with OpenJDK 19), switching the providers 
by using the `--provider "provider"` argument, to see the whole list just run `--help` as arguments

## Current Providers
The current supported backends are
* STAR (Rennes)

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
java -jar target/scala-3.2.0 snapshot.jar <arguments>
```

## The Language
The main part of the Scala part of the project is to lex and parse a little 
language to construct the commands, it supports unicode for multiple way to create the 
command for example
* `and`
* `&`
* `∩`

all lex to the `AND` operator,

* `or`
* `|`
* `∪`

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
- [ ] Update provider
- [ ] Implementing more providers
