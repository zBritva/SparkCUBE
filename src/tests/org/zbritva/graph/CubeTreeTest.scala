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
  var tree: CubeTree = _

  override def setUp: Unit = {
    val lst: List[String] = List(
      "A", "B", "C", "D"
    )
    tree = new CubeTree(lst)
    println(tree.getTree._level_list.toList.sortBy(_._1))
  }

  def testCalculation: Unit = {

    val treestring = "List((1,Set(List(*))), (2,Set(List(C), List(A), List(B), List(D))), (3,Set(List(A, C), List(B, C), List(B, D), List(C, D), List(A, B), List(A, D))), (4,Set(List(B, C, D), List(A, C, D), List(A, B, D), List(A, B, C))), (5,Set(List(A, B, C, D))))"
    val result = tree.getTree.getLevels().toList.sortBy(_._1).toString()
    println(result)
    assert(treestring == result)
  }
}