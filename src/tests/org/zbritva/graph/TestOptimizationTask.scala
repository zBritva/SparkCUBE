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
  }

  def walkOnTree(node: TreeNode): Unit = {
    //ROOT NODE
    if (node.checkColumns( List( "A", "B", "C", "D") ) ){
      //TODO set cost value
    }

    //recursive walk to children
    if(node.getChildren() != null && node.getChildren().nonEmpty){
      for(child <- node.getChildren()){
        walkOnTree(child._3)
      }
    }
  }
}
