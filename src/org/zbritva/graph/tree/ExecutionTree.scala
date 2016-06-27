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
        val levelChildCount = level._2.head.getChildren().length
//        val objectiveFunctionSize: Int = (currentLevelNodesCount + currentLevelNodesCount * additionalCopies + 1) * levelChildCount
        val objectiveFunctionSize: Int = currentLevelNodesCount * (additionalCopies + 1) + 1 //+1 because one columns for value of constraint
        //because we must provide computing each node in previous level, we use same constraint count
        //one constraint for one node of previous level
        val contraintsCount: Int = levels(level._1 - 1)._2.size

        val simplexTable: Array[Array[Double]] = Array.fill(contraintsCount + 1, objectiveFunctionSize) {
          0
        }

        val objectiveFunctionIndex: Int = simplexTable.length - 1

        //define which node of previous level correspond to number of constraint
        var groupByToConstraint: Map[String,Int] = Map[String,Int]()

        var constraintIndex: Int = 0
        for(previousLevelNode <- levels(level._1 - 1)._2) {
          groupByToConstraint = groupByToConstraint.+(previousLevelNode.node_columns.mkString(";") -> constraintIndex)
          constraintIndex += 1
        }

        //set equal all constraints values to 1
        //that means we must assign one worker for one task and vise-versa
        for (constraint <- Range(0, simplexTable.length - 1)) {
          simplexTable(constraint)(0) = 1
        }

        var index: Int = 1 //because 0 is OF value and always equal to 0 (it is just Simplex class requirements)
        constraintIndex = 0
        for (functionValue <- level._2) {
          for (child <- functionValue.getChildren()) {
            simplexTable(objectiveFunctionIndex)(index) = functionValue.getCostOfWitoutSorting() //cost without sorting
            constraintIndex = groupByToConstraint(child._3.node_columns.mkString(";"))
            simplexTable(constraintIndex)(index) = 1
          }
          index += 1
//          constraintIndex += 1
        }
        //so, next is additional copies costs

        //shift index to position of cost additional values
        index = currentLevelNodesCount + 1
        for (copy <- Range(0, additionalCopies)) {
          constraintIndex = 0; //reset constraint index
          for (functionValue <- level._2) {
            for (child <- functionValue.getChildren()) {
              simplexTable(objectiveFunctionIndex)(index) = functionValue.getCostOfSorting() //cost with sorting
              constraintIndex = groupByToConstraint(child._3.node_columns.mkString(";"))
              simplexTable(constraintIndex)(index) = 1
            }
//            constraintIndex += 1
            index += 1
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
