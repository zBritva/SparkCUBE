package org.zbritva.graph.tree

import scala.collection.immutable

//import javax.swing.tree.TreeNode

//import org.zbritva.graph.CubeTree

/**
  * Created by iigaliev on 20.05.2016.
  */
class ExecutionTree(root: TreeNode, level_list: Map[Int,immutable.Set[List[String]]]) {

  var _level_list: Map[Int,immutable.Set[List[String]]] = level_list
  var _root: TreeNode = root

  def getRoot(): TreeNode ={
    this._root
  }

  def getLevels(): Map[Int,immutable.Set[List[String]]] ={
    this._level_list
  }

  def _solveSimpexOptimizationTask(): Unit ={
//    for(level <- _level_list.toList.sorted(Ordering.Int)){
//
//    }
  }
}
