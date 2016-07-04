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
      "A", "B", "C"
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
    if (node.checkColumns(List("A", "B", "C"))) {
      node.setCostOfWitoutSorting(12)
      node.setCostOfSorting(26)
    }

    if (node.checkColumns(List("*"))) {
      return
    }

    if (node.checkColumns(List("A", "B"))) {
      node.setCostOfWitoutSorting(2)
      node.setCostOfSorting(10)
    }

    if (node.checkColumns(List("B", "C"))) {
      node.setCostOfWitoutSorting(13)
      node.setCostOfSorting(20)
    }

    if (node.checkColumns(List("A", "C"))) {
      node.setCostOfWitoutSorting(5)
      node.setCostOfSorting(12)
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

    //recursive walk to children
    if (node.getChildren() != null && node.getChildren().nonEmpty) {
      for (child <- node.getChildren()) {
        walkOnTree(child._3)
      }
    }
  }
}
