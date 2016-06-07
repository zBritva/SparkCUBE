package tests.org.zbritva.graph

/**
  * Created by iigaliev on 23.05.2016.
  */


import org.junit.Assert._
import org.junit.Test
import junit.framework.TestCase
import org.junit.Before
import org.zbritva.graph.CubeTree

class CubeTreeTest extends TestCase {
  var cubtree: CubeTree = _

  override def setUp: Unit = {
    val lst: List[String] = List(
      "A", "B", "C", "D"
    )
    cubtree = new CubeTree(lst)
    val tree = cubtree.getTree
    val task = tree._constructSimpexOptimizationTask()
    print(task)
    println(tree._level_list.toList.sortBy(_._1))
  }

  def testCalculation: Unit = {
    val treestring = "List((0,Set(List(*))), (1,Set(List(C), List(A), List(B), List(D))), (2,Set(List(A, C), List(B, C), List(B, D), List(C, D), List(A, B), List(A, D))), (3,Set(List(B, C, D), List(A, C, D), List(A, B, D), List(A, B, C))), (4,Set(List(A, B, C, D))))"
    val result = cubtree.getTree.getLevels().toList.sortBy(_._1).toString()
    println(result)
    assert(treestring == result)
  }
}