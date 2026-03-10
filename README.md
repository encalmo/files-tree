<a href="https://github.com/encalmo/files-tree">![GitHub](https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white)</a> <a href="https://central.sonatype.com/artifact/org.encalmo/files-tree_3" target="_blank">![Maven Central Version](https://img.shields.io/maven-central/v/org.encalmo/files-tree_3?style=for-the-badge)</a> <a href="https://encalmo.github.io/files-tree/scaladoc/org/encalmo/utils.html" target="_blank"><img alt="Scaladoc" src="https://img.shields.io/badge/docs-scaladoc-red?style=for-the-badge"></a>

# files-tree

A small Scala 3 utility to draw a files tree, like:

```
├── bar
│   └── foo
│       └── foo.bar
│
├── foo
│   └── zoo
│       └── bar.txt
│
├── zoo
│   └── foo
│       └── bar
│           └── zoo.scala
│
└── zoo.scala
```

## Motivation

When building CLI tools, documentation generators, or tests you often need to show a directory layout or a set of paths as a readable tree. Calling external commands like `tree` is not always possible or desirable: it may be unavailable on the platform, produce different output, or you might only have a list of paths (e.g. from an API or a test) rather than a real filesystem. **files-tree** gives you a small, dependency-free Scala 3 API to build and render such trees from any sequence of paths, or by walking a directory—so your app can show consistent, cross-platform file trees without relying on the environment.

## Table of contents

- [Motivation](#motivation)
- [Dependencies](#dependencies)
- [Usage](#usage)
- [Examples](#examples)
- [Project content](#project-content)

## Dependencies

   - [Scala](https://www.scala-lang.org) >= 3.3.5

## Usage

Use with SBT

    libraryDependencies += "org.encalmo" %% "files-tree" % "0.9.3"

or with SCALA-CLI

    //> using dep org.encalmo::files-tree:0.9.3

## Examples

```scala
val pathTree = FilesTree.compute(
    Seq(
    Paths.get("/test"),
    Paths.get("/test", "foo", "bar.txt"),
    Paths.get("foo.bar"),
    Paths.get("/test", "test.scala")
    )
)

FilesTree.draw(pathTree) shouldBe
    """├── foo.bar
      |└── test
      |    ├── foo
      |    │   └── bar.txt
      |    │
      |    └── test.scala""".stripMargin
```

## Project content

```
├── .github
│   └── workflows
│       ├── pages.yaml
│       ├── release.yaml
│       └── test.yaml
│
├── .gitignore
├── .scalafmt.conf
├── FilesTree.scala
├── FilesTree.test.scala
├── LICENSE
├── project.scala
├── README.md
└── test.sh
```

