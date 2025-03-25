package org.encalmo.utils

import java.nio.file.Paths

class FilesTreeSpec extends munit.FunSuite {

  extension [T](t0: T)
    inline def shouldBe(t1: T) =
      assertEquals(t0, t1)

  extension [T](ts0: Seq[T])
    inline def containsOnly(ts1: Seq[T]) =
      assert(ts0.sameElements(ts1))

  test("tree of current folder") {
    println(
      FilesTree.tree(
        root = new java.io.File(".").toPath(),
        isAllowed = GitIgnore.fromCurrentDirectory().excludeGitFolder().isAllowed,
        includeRoot = false
      )
    )
  }

  test("compute a tree") {
    FilesTree.compute(Seq(Paths.get("test.scala"))) shouldBe List(0 -> "test.scala")
    FilesTree.compute(Seq(Paths.get("/test", "test.scala"))) shouldBe List(0 -> "test", 1 -> "test.scala")
    FilesTree.compute(Seq(Paths.get("/test"), Paths.get("/test", "test.scala"))) containsOnly (List(
      0 -> "test",
      1 -> "test.scala"
    ))
    FilesTree.compute(Seq(Paths.get("/test", "test.scala"), Paths.get("/test"))) containsOnly (List(
      0 -> "test",
      1 -> "test.scala"
    ))
    FilesTree.compute(Seq(Paths.get("/test", "test.scala"), Paths.get("/test"))) containsOnly (List(
      0 -> "test",
      1 -> "test.scala"
    ))
    FilesTree.compute(Seq(Paths.get("/test"), Paths.get("/test", "test.scala"))) containsOnly (List(
      0 -> "test",
      1 -> "test.scala"
    ))
    FilesTree.compute(
      Seq(Paths.get("/test"), Paths.get("/test", "foo", "bar.txt"), Paths.get("/test", "test.scala"))
    ) containsOnly (List(0 -> "test", 1 -> "foo", 2 -> "bar.txt", 1 -> "test.scala"))
    FilesTree.compute(
      Seq(
        Paths.get("/test"),
        Paths.get("/test", "foo", "bar.txt"),
        Paths.get("foo.bar"),
        Paths.get("/test", "test.scala")
      )
    ) containsOnly (List(
      0 -> "foo.bar",
      0 -> "test",
      1 -> "foo",
      2 -> "bar.txt",
      1 -> "test.scala"
    ))
    FilesTree.compute(
      Seq(
        Paths.get("/test"),
        Paths.get("/test", "foo", "bar.txt"),
        Paths.get("/bar", "foo.bar"),
        Paths.get("/test", "test.scala")
      )
    ) containsOnly (
      List(0 -> "bar", 1 -> "foo.bar", 0 -> "test", 1 -> "foo", 2 -> "bar.txt", 1 -> "test.scala")
    )
  }

  test("draw a tree 1") {
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
  }

  test("draw a tree 2") {
    val pathTree = FilesTree.compute(
      Seq(
        Paths.get("/test"),
        Paths.get("/test", "foo", "bar.txt"),
        Paths.get("/bar", "foo.bar"),
        Paths.get("/test", "test.scala")
      )
    )
    FilesTree.draw(pathTree) shouldBe
      """├── bar
          |│   └── foo.bar
          |│
          |└── test
          |    ├── foo
          |    │   └── bar.txt
          |    │
          |    └── test.scala""".stripMargin
  }

  test("draw a tree 3") {
    val pathTree = FilesTree.compute(Seq(Paths.get("/test")))
    FilesTree.draw(pathTree) shouldBe
      """└── test""".stripMargin
  }

  test("draw a tree 4") {
    val pathTree = FilesTree.compute(
      Seq(
        Paths.get("zoo.scala"),
        Paths.get("/foo", "zoo", "bar.txt"),
        Paths.get("/bar", "foo", "foo.bar"),
        Paths.get("/zoo", "foo", "bar", "zoo.scala")
      )
    )
    val tree = FilesTree.draw(pathTree)
    println(tree)
    tree shouldBe
      """├── bar
          |│   └── foo
          |│       └── foo.bar
          |│
          |├── foo
          |│   └── zoo
          |│       └── bar.txt
          |│
          |├── zoo
          |│   └── foo
          |│       └── bar
          |│           └── zoo.scala
          |│
          |└── zoo.scala""".stripMargin
  }

}
