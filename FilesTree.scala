package org.encalmo.utils

import java.nio.file.{Path, Paths}

import scala.annotation.tailrec
import scala.jdk.StreamConverters.*

object FilesTree {

  type Node = (Int, String)
  type Tree = List[Node]

  private val middleNode = "├── "
  private val endNode = "└── "
  private val link = "│   "
  private val space = " " * link.length

  private val root = Paths.get("/")

  /** Draw a tree of files and folders starting at root
    *
    * @param root
    *   folder where to start
    * @param maxDepth
    *   max tree depth
    * @param visitOptions
    *   java file visti options
    * @param isAllowed
    *   whether to include given path and its subpaths
    * @param includeRoot
    *   whether to render root folder at the top
    * @return
    */
  final def tree(
      root: java.nio.file.Path,
      maxDepth: Int = Int.MaxValue,
      visitOptions: Seq[java.nio.file.FileVisitOption] = Seq.empty,
      isAllowed: Path => Boolean = _ => true,
      includeRoot: Boolean = true
  ): String =
    val paths = java.nio.file.Files
      .walk(root, maxDepth, visitOptions: _*)
      .filter(p => (includeRoot || p != root) && isAllowed(p))
      .toScala(Seq)
      .map(path =>
        if (includeRoot) path
        else root.relativize(path)
      )
    draw(compute(paths))

  private inline def sort(paths: Seq[Path]): Seq[Path] =
    paths.sortWith((pl, pr) => comparePaths(pl, pr, 0))

  /** Compute tree from file paths */
  final def compute(paths: Seq[Path]): Tree = {

    def leafs(prefix: Path, p2: Path): Tree =
      (0 until p2.getNameCount).toList
        .map(i => (i + prefix.getNameCount, p2.getName(i).toString))

    sort(paths)
      .foldLeft((List.empty[Node], Option.empty[Path])) { case ((tree, prevPathOpt), path) =>
        (
          prevPathOpt
            .map { prevPath =>
              val (prefix, _, outstandingPath) = commonPrefix(root, prevPath, path)
              tree ::: leafs(prefix, outstandingPath)
            }
            .getOrElse(leafs(root, path)),
          Some(path)
        )
      }
      ._1
  }

  /** Draw a tree */
  final def draw(pathsTree: Tree): String = {

    def drawLine(node: String, label: String, marks: List[Int]): (String, List[Int]) =
      ((0 until marks.max).map(i => if (marks.contains(i)) link else space).mkString + node + label, marks)

    def draw2(label: String, ls: (List[Int], String)): (String, List[Int]) =
      ((0 until ls._1.max).map(i => if (ls._1.contains(i)) link else space).mkString + ls._2 + label, ls._1)

    def append(lineWithMarks: (String, List[Int]), result: String): (String, List[Int]) =
      (trimRight(lineWithMarks._1) + "\n" + result, lineWithMarks._2)

    pathsTree.reverse
      .foldLeft(("", List.empty[Int])) { case ((result, marks), (offset, label)) =>
        marks match {
          case Nil => drawLine(endNode, label, offset :: Nil)
          case head :: tail =>
            append(
              if (offset == head) drawLine(middleNode, label, marks)
              else if (offset < head)
                draw2(
                  label,
                  tail match {
                    case Nil                   => (offset :: Nil, endNode)
                    case x :: _ if x == offset => (tail, middleNode)
                    case _                     => (offset :: tail, endNode)
                  }
                )
              else {
                val l1 = drawLine(endNode, label, offset :: marks)
                val l2 = drawLine(space, "", offset :: marks)
                (l1._1 + "\n" + l2._1, l1._2)
              },
              result
            )
        }
      }
      ._1
  }

  @tailrec
  private def comparePaths(path1: Path, path2: Path, i: Int): Boolean = {
    val c = path1.getName(i).toString.compareToIgnoreCase(path2.getName(i).toString)
    val pc1 = path1.getNameCount
    val pc2 = path2.getNameCount
    if (pc1 - 1 == i || pc2 - 1 == i) {
      if (c != 0) c < 0 else pc1 < pc2
    } else {
      if (c != 0) c < 0 else comparePaths(path1, path2, i + 1)
    }
  }

  @tailrec
  private def commonPrefix(prefix: Path, path1: Path, path2: Path): (Path, Path, Path) =
    if (path1.getNameCount > 0 && path2.getNameCount > 0) {
      if (path1.getName(0) != path2.getName(0)) (prefix, path1, path2)
      else
        commonPrefix(
          prefix.resolve(path1.subpath(0, 1)),
          if (path1.getNameCount == 1) path1 else path1.subpath(1, path1.getNameCount),
          if (path2.getNameCount == 1) path2 else path2.subpath(1, path2.getNameCount)
        )
    } else (prefix, path1, path2)

  private inline def trimRight(string: String): String = string.reverse.dropWhile(_ == ' ').reverse
}
