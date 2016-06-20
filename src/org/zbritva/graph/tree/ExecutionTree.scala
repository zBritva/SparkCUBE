package org.zbritva.graph.tree

import org.zbritva.graph.Simplex

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
  var _task: Map[Int, (Array[Array[Double]], Array[Double])] = null

  def getRoot(): TreeNode = {
    this._root
  }

  def getLevels(): Map[Int, immutable.Set[List[String]]] = {
    this._level_list
  }

  def _constructSimpexOptimizationTask(): Map[Int, (Array[Array[Double]],Array[Double])] = {
    val levels = _level_list_tree.toList.sortBy(_._1)
    var task: Map[Int, (Array[Array[Double]], Array[Double])] = Map[Int, (Array[Array[Double]], Array[Double])]()

    for (level <- levels) {
      if (level._1 == 0) {
        //skip zero level, because do nothing

      } else {
        //other cases
        val additionalCopies = level._1 - 1
        val currentLevelNodesCount = level._2.size
        val levelChildCount = level._2.head.getChilds().length
//        val objectiveFunctionSize: Int = (currentLevelNodesCount + currentLevelNodesCount * additionalCopies + 1) * levelChildCount
        val objectiveFunctionSize: Int = (currentLevelNodesCount * levelChildCount) * (additionalCopies + 1) + 1 //+1 because one columns for value of constraint
        val objectiveFunctionIndex: Int = currentLevelNodesCount
        val contraintsCount: Int = currentLevelNodesCount + 1
        //construct objective function
        //        val objectiveFunction: Array[Double] = Array.fill(objectiveFunctionSize) {
        //          0
        //        }

        val simplexTable: Array[Array[Double]] = Array.fill(contraintsCount, objectiveFunctionSize) {
          0
        }

        //set equal all constraints values to 1
        //that means we must assign one worker for one task and vise-versa
        for (constraint <- Range(0, simplexTable.length - 1)) {
          simplexTable(constraint)(0) = 1
        }

        //TODO fix converter
        var index: Int = 1 //because 0 is OF value and always equal to 0 (it is just Simplex class requirements)
        var constIndex: Int = 0
        for (functionValue <- level._2) {
          for (child <- functionValue.getChilds()) {
            simplexTable(objectiveFunctionIndex)(index) = child._1 //child._1 - cost without sorting
            simplexTable(constIndex)(index) = 1
            index += 1
          }
          constIndex += 1
        }
        //so, next is additional copies costs

        //shift index to position of cost additional values
        index = (currentLevelNodesCount * levelChildCount) + 1
        for (copy <- Range(0, additionalCopies)) {
          constIndex = 0; //reset constraint index
          for (functionValue <- level._2) {
            for (child <- functionValue.getChilds()) {
              simplexTable(objectiveFunctionIndex)(index) = child._2 //child._2 - cost with sorting
              simplexTable(constIndex)(index) = 1
              index += 1
            }
            constIndex += 1
          }
        }

        //TODO check solution for 0 level computing
        val simplexTask = new Simplex(simplexTable)
        val resultSimplex = simplexTask.Calculate()

        task = task.+(level._1 ->(simplexTable, resultSimplex._2))
        //construct constraints
      }
    }

    this._task = task
    task
  }

}
