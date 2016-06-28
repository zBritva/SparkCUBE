package tests.org.zbritva.graph

import junit.framework.TestCase
import org.zbritva.graph.CubeTree
import org.zbritva.graph.tree.TreeNode

/**
  * Created by iigaliev on 27.06.2016.
  */
class TestOptimizationTask extends TestCase {

  var cubtree: CubeTree = null

  override def setUp: Unit = {
    //List of columns of cubing
    val lst: List[String] = List(
      "A", "B", "C", "D"
    )

    cubtree = new CubeTree(lst)
  }

  def test(): Unit = {
    val tree = cubtree.getTree

    walkOnTree(tree.getRoot())

    val task = tree._constructSimpexOptimizationTask()

    println(task)

    tree.solveOptimizationTask()

    print("DONE")
  }

  def walkOnTree(node: TreeNode): Unit = {
    //ROOT NODE
    if (node.checkColumns(List("A", "B", "C", "D"))) {
      node.setCostOfWitoutSorting(12)
      node.setCostOfSorting(26)
    }

    if (node.checkColumns(List("*"))) {
      return
    }

    if (node.checkColumns(List("A", "B", "C"))) {
      node.setCostOfWitoutSorting(3)
      node.setCostOfSorting(9)
    }

    if (node.checkColumns(List("D", "B", "C"))) {
      node.setCostOfWitoutSorting(6)
      node.setCostOfSorting(14)
    }

    if (node.checkColumns(List("A", "D", "C"))) {
      node.setCostOfWitoutSorting(1)
      node.setCostOfSorting(13)
    }

    if (node.checkColumns(List("A", "B", "D"))) {
      node.setCostOfWitoutSorting(7)
      node.setCostOfSorting(13)
    }

    if (node.checkColumns(List("A", "B"))) {
      node.setCostOfWitoutSorting(2)
      node.setCostOfSorting(10)
    }

    if (node.checkColumns(List("B", "C"))) {
      node.setCostOfWitoutSorting(13)
      node.setCostOfSorting(20)
    }

    if (node.checkColumns(List("C", "D"))) {
      node.setCostOfWitoutSorting(20)
      node.setCostOfSorting(25)
    }

    if (node.checkColumns(List("A", "D"))) {
      node.setCostOfWitoutSorting(10)
      node.setCostOfSorting(18)
    }

    if (node.checkColumns(List("A", "C"))) {
      node.setCostOfWitoutSorting(5)
      node.setCostOfSorting(12)
    }

    if (node.checkColumns(List("D", "B"))) {
      node.setCostOfWitoutSorting(14)
      node.setCostOfSorting(19)
    }

    if (node.checkColumns(List("A"))) {
      node.setCostOfWitoutSorting(10)
      node.setCostOfSorting(12)
    }

    if (node.checkColumns(List("B"))) {
      node.setCostOfWitoutSorting(8)
      node.setCostOfSorting(10)
    }

    if (node.checkColumns(List("C"))) {
      node.setCostOfWitoutSorting(6)
      node.setCostOfSorting(8)
    }

    if (node.checkColumns(List("D"))) {
      node.setCostOfWitoutSorting(4)
      node.setCostOfSorting(6)
    }

    //recursive walk to children
    if (node.getChildren() != null && node.getChildren().nonEmpty) {
      for (child <- node.getChildren()) {
        walkOnTree(child._3)
      }
    }
  }
}
