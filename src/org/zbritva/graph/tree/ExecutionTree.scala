package org.zbritva.graph.tree

import scala.collection.mutable
import scala.collection.immutable

//import javax.swing.tree.TreeNode

//import org.zbritva.graph.CubeTree

/**
  * Created by iigaliev on 20.05.2016.
  */
class ExecutionTree(root: TreeNode, level_list: Map[Int, immutable.Set[List[String]]], level_list_tree: Map[Int, mutable.Set[TreeNode]]) {

  var _root: TreeNode = root
  var _level_list: Map[Int, immutable.Set[List[String]]] = level_list
  var _level_list_tree: Map[Int, mutable.Set[TreeNode]] = level_list_tree

  def getRoot(): TreeNode = {
    this._root
  }

  def getLevels(): Map[Int, immutable.Set[List[String]]] = {
    this._level_list
  }

  def _solveSimpexOptimizationTask(): Unit = {
//    val levels = _level_list_tree.toList.sortBy(_._1)
//    for (level <- levels) {
//      if (level._1 == 0) {
//        //skip zero level, because do nothing
//
//      } else {
//        //other cases
//        val additionalCopies = level._1 - 1
//        val currentLevelNodesCount = level._2.size
//        val objectiveFunctionSize: Int = currentLevelNodesCount + currentLevelNodesCount * additionalCopies
//
//        val objectiveFunction = Array[Double](objectiveFunctionSize)
//        //fill objective function coefficients
//        for(functionValue <- level._2){
//
//        }
//
//      }
//    }
  }
}
