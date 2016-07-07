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
        //TODO: need keep in mine about edges to children nodes
        //other cases
        val additionalCopies = level._1 - 1
        val prevLevelNodesCount = levels(level._1 - 1)._2.size
        val levelNodesCount = level._2.size
        val levelChildCount = level._2.head.getChildren().length
//        val objectiveFunctionSize: Int = (currentLevelNodesCount + currentLevelNodesCount * additionalCopies + 1) * levelChildCount
        val objectiveFunctionSize: Int = levelNodesCount * prevLevelNodesCount * (additionalCopies + 1) + levelNodesCount + 1 //+1 because one columns for value of constraint
        //because we must provide computing each node in previous level, we use same constraint count
        //one constraint for one node of previous level
        val constraintsCount: Int = levelChildCount + levelNodesCount * (additionalCopies + 1)

        val simplexTable: Array[Array[Double]] = Array.fill(constraintsCount + 1, objectiveFunctionSize) {
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

        for(constraintIndex <- Range(0, levelChildCount)){
          val shift: Int = (constraintIndex) * levelNodesCount
          for(variableIndex <- Range(1, levelNodesCount + 1)){
            simplexTable(constraintIndex)(variableIndex + shift) = 1
          }
        }

        for(row <- Range(0, levelNodesCount * (additionalCopies + 1))){
          for(col <- Range(0, levelChildCount + 1 )){ //+1 для доп. переменных, которые добавлены, после придения в канонический вид и еше +1 так как правая граница не учитывается
            val rrow: Int = row + levelChildCount
            val rcol: Int = row + col * levelNodesCount * (additionalCopies + 1) + 1
            simplexTable(rrow)(rcol) = 1
          }
        }

        //TODO check solution for 0 level computing
//        val simplexTask = new Simplex(simplexTable)
//        val resultSimplex = simplexTask.Calculate()

        task = task.+(level._1 ->(simplexTable, null))
        //construct constraints
      }
    }

    this._task = task
    task
  }

  def solveOptimizationTask(): Boolean ={
    try{
      for(level <- _task.keys.toList.sorted){
        print("SOLVE ")
        print(level)
        println(" TASK")

        var conditions = _task(level)._1

        val simplex = new Simplex(conditions)
        val solution = simplex.Calculate()

        _task.updated(level,(conditions, solution))
      }
      true
    }
    catch {
      case e: Exception =>
        println("EXCEPTION:")
        throw e
    }
  }

  def constructOptimizedTree(): TreeNode ={
    null
  }
}
