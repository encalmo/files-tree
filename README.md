<a href="https://github.com/encalmo/files-tree">![GitHub](https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white)</a> <a href="https://central.sonatype.com/artifact/org.encalmo.utils/files-tree_3" target="_blank">![Maven Central Version](https://img.shields.io/maven-central/v/org.encalmo.utils/files-tree_3?style=for-the-badge)</a> <a href="https://encalmo.github.io/files-tree/scaladoc/org/encalmo/utils.html" target="_blank"><img alt="Scaladoc" src="https://img.shields.io/badge/docs-scaladoc-red?style=for-the-badge"></a>

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

## Dependencies

   - [Scala](https://www.scala-lang.org) >= 3.3.5

## Usage

Use with SBT

    libraryDependencies += "org.encalmo" %% "files-tree" % "0.9.1"

or with SCALA-CLI

    //> using dep org.encalmo::files-tree:0.9.1

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