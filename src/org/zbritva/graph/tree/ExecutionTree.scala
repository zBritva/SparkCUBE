package org.zbritva.graph.tree

import org.zbritva.graph.Simplex

import scala.collection.mutable
import scala.collection.immutable
import scala.util.control.Breaks._

//import javax.swing.tree.TreeNode

//import org.zbritva.graph.CubeTree

/**
  * Created by iigaliev on 20.05.2016.
  */
class ExecutionTree(root: TreeNode, level_list: Map[Int, immutable.Set[List[String]]], level_list_tree: Map[Int, mutable.Set[TreeNode]]) {

  var _root: TreeNode = root
  var _level_list: Map[Int, immutable.Set[List[String]]] = level_list
  var _level_list_tree: Map[Int, mutable.Set[TreeNode]] = level_list_tree
  var _task: Map[Int, (Array[Array[Double]], Array[Double], Map[String, Int], Map[String, Int])] = null

  def getRoot(): TreeNode = {
    this._root
  }

  def getLevels(): Map[Int, immutable.Set[List[String]]] = {
    this._level_list
  }

  def _constructSimpexOptimizationTask(): Map[Int, (Array[Array[Double]], Array[Double], Map[String, Int], Map[String, Int])] = {
    val levels = _level_list_tree.toList.sortBy(_._1)
    var task: Map[Int, (Array[Array[Double]], Array[Double], Map[String, Int], Map[String, Int])] = Map[Int, (Array[Array[Double]], Array[Double], Map[String, Int], Map[String, Int])]()

    for (level <- levels) {

      if (level._1 != 0) {
        //TODO: need keep in mine about edges to children nodes
        //other cases
        val additionalCopies = level._1 - 1
        val levelNodesCount = level._2.size
        val prevLevelNodesCount = levels(level._1 - 1)._2.size
        val levelChildCount = level._2.head.getChildren().length
        //        val objectiveFunctionSize: Int = (currentLevelNodesCount + currentLevelNodesCount * additionalCopies + 1) * levelChildCount
        val objectiveFunctionSize: Int = levelNodesCount * prevLevelNodesCount * (additionalCopies + 1) + levelNodesCount * (additionalCopies + 1) + 1 //+1 because one columns for value of constraint
        //because we must provide computing each node in previous level, we use same constraint count
        //one constraint for one node of previous level
        val constraintsCount: Int = levelNodesCount * (additionalCopies + 1) + prevLevelNodesCount

        val simplexTable: Array[Array[Double]] = Array.fill(constraintsCount + 1, objectiveFunctionSize) {
          0
        }

        val OFIndex: Int = simplexTable.length - 1

        //define which node of previous level correspond to number of constraint
        var groupByToConstraint: Map[String, Int] = Map[String, Int]()
        var groupByToVariable: Map[String, Int] = Map[String, Int]()

        var constraintIndex: Int = 0
        for (previousLevelNode <- levels(level._1 - 1)._2) {
          groupByToConstraint = groupByToConstraint.+(previousLevelNode.node_columns.mkString(";") -> constraintIndex)
          constraintIndex += 1
        }

        var variableIndex: Int = 1
        for (levelNode <- level._2) {
          groupByToVariable = groupByToVariable.+(levelNode.node_columns.mkString(";") -> variableIndex)
          variableIndex += 1
        }

        //set equal all constraints values to 1
        //that means we must assign one worker for one task and vise-versa
        for (constraint <- Range(0, simplexTable.length - 1)) {
          simplexTable(constraint)(0) = 1
        }

        //set function values
        for (copy <- Range(0, additionalCopies + 1)) {
          for (levelNode <- level._2) {
            for (child <- levelNode.getChildren()) {
              constraintIndex = groupByToConstraint(child._3.node_columns.mkString(";"))
              variableIndex = groupByToVariable(levelNode.node_columns.mkString(";"))
              var value = -1
              if (copy == 0)
                value = levelNode.getCostOfWitoutSorting()
              else
                value = levelNode.getCostOfSorting()

              //constraintIndex * levelNodesCount * (additionalCopies + 1) + variableIndex + (copy * levelNodesCount)
              simplexTable(OFIndex)(constraintIndex * levelNodesCount * (additionalCopies + 1) + variableIndex + (copy * levelNodesCount)) = -value
              simplexTable(constraintIndex)(constraintIndex * levelNodesCount * (additionalCopies + 1) + variableIndex + (copy * levelNodesCount)) = 1
              //              simplexTable(OFIndex)(variableIndex + (levelNodesCount * constraintIndex) + (levelNodesCount * copy)) = value
              //              simplexTable(constraintIndex)(variableIndex + (levelNodesCount * constraintIndex) + (levelNodesCount * copy)) = 1
            }
          }
        }

        val additionalVariablesBeginIndex: Int = 1 + levelNodesCount * (additionalCopies + 1) * prevLevelNodesCount

        var rowIndex = prevLevelNodesCount //because we were make one iteration
        for (copy <- Range(0, additionalCopies + 1)) {
          for (levelNode <- level._2) {
            variableIndex = groupByToVariable(levelNode.node_columns.mkString(";"))
            simplexTable(rowIndex)(variableIndex - 1 + additionalVariablesBeginIndex) = 1
            for (child <- levelNode.getChildren()) {
              constraintIndex = groupByToConstraint(child._3.node_columns.mkString(";"))
              //shift constraint index
              //constraintIndex += baseConstraintShift + (prevLevelNodesCount * copy)
              val colIndex = constraintIndex * levelNodesCount * (additionalCopies + 1) + variableIndex + (copy * levelNodesCount)

              simplexTable(rowIndex)(colIndex) = 1
            }
            rowIndex += 1
          }
        }

        //groupByToVariable, groupByToConstraint - need for decode simplex table results and change order of sorting node columns
        task = task.+(level._1 ->(simplexTable, null, groupByToVariable, groupByToConstraint))
        //construct constraints
      }
    }

    this._task = task
    task
  }

  def _resortNodesColumns(parent: String, child: String): (String, String) = {
    if (child == "*") {
      return (parent, child)
    }
    else {
      val child_columns = child.split(";")
      var new_parent = parent

      for (ch <- child_columns) {
        new_parent = new_parent.replace(ch, "")
      }


      if (new_parent(0) == ';') {
        new_parent = new_parent.substring(1)
      }

      new_parent = child + ";" + new_parent

      //clear extra separators
      new_parent = new_parent.replace(";'", "'")
      new_parent = new_parent.replace(";;", ";")

      val sepIndex = new_parent.lastIndexOf(";")
      if (sepIndex == new_parent.length - 1) {
        new_parent = new_parent.substring(0, new_parent.length - 1)
      }

      return (new_parent, child)
    }
  }

  def _constructExecutionTree(): Map[Int, List[(String, String)]] = {
    try {
      val tree_level = _level_list_tree.toList.sortBy(_._1)
      var executionTree: Map[Int, List[(String, String)]] = Map[Int, List[(String, String)]]()
      for (level <- _level_list_tree.keys.toList.sorted) {
        if (level != 0) {
          print("RESORT ")
          print(level)
          println(" TASK")

          val result = _task(level)._2
          var groupByToVariable = _task(level)._3.map(_.swap)
          val groupByToConstraint = _task(level)._4.map(_.swap)

          val additionalCopies = tree_level(level)._1 - 1
          val levelNodesCount = tree_level(level)._2.size
          val prevLevelNodesCount = tree_level(tree_level(level)._1 - 1)._2.size
          val levelChildCount = tree_level(level)._2.head.getChildren().length

          val grSize = groupByToVariable.size
          for (ac <- Range(1, additionalCopies + 1)) {
            for (gr: Int <- groupByToVariable.keys.toList) {
              var valueSuffic = ""
              for (ac <- Range(0, ac)) {
                valueSuffic += "'"
              }
              groupByToVariable = groupByToVariable.+((gr + grSize).toInt -> (groupByToVariable(gr) + valueSuffic))
            }
          }

          var levelExecutionTree: List[(String, String)] = List[(String, String)]()
          for (value <- Range(0, result.length - (levelNodesCount * (additionalCopies + 1)))) {
            if (result(value).toString != "0.0") {
              var constraintIndex = (value + 1) / (levelNodesCount * (additionalCopies + 1))
              if ((value + 1) == levelNodesCount * (additionalCopies + 1)) {
                constraintIndex = 0
              }
              val variableIndex = (value + 1) - (levelNodesCount * (additionalCopies + 1)) * constraintIndex

              var tmp: Tuple2[String,String] = null
              try {
                tmp = (groupByToVariable(variableIndex), groupByToConstraint(constraintIndex))
              }
              catch {
                case e: NoSuchElementException =>
                  println("Key not found")
                  println("variableIndex:" + variableIndex)
                  println("Data:" + groupByToVariable.toString)
                  println("constraintIndex:" + constraintIndex)
                  println("Data:" + groupByToConstraint.toString)
                  throw e
              }


              //TODO replace child of current level by parents of previous level
              if (level - 1 != 0) {
                val prevLevelExecutionTree = executionTree(level - 1)

                breakable {
                  for (prevLevelNode <- prevLevelExecutionTree) {
                    val prev = prevLevelNode._1.replace("'", "").split(";").toSet
                    val curr = tmp._2.replace("'", "").split(";").toSet

                    //Intersect two list
                    val intersection = prev.intersect(curr)

                    if (intersection.size == prev.size && intersection.size == curr.size) {
                      tmp = (tmp._1, prevLevelNode._1)
                      break()
                    }
                  }
                }
              }
              tmp = _resortNodesColumns(tmp._1, tmp._2)
              //END
              levelExecutionTree = levelExecutionTree.::(tmp)
              print(tmp._1)
              print(" -> ")
              println(tmp._2)
            }
          }

          executionTree = executionTree + (level -> levelExecutionTree)
          println("-------------")
        }
      }
      executionTree
    }
    catch {
      case e: Exception =>
        println("EXCEPTION:")
        throw e
    }
  }

  def solveOptimizationTask(): Map[Int, List[(String, String)]] = {
    try {
      for (level <- _task.keys.toList.sorted) {
        print("SOLVE ")
        print(level)
        println(" TASK")

        val conditions = _task(level)._1

        val simplex = new Simplex(conditions)
        val solution = simplex.Calculate()

        //TODO resort order of node columns, according solution of simplex optimization task (75%)
        _task = _task.updated(level, (conditions, solution._2, _task(level)._3, _task(level)._4))
      }

      return _constructExecutionTree()
    }
    catch {
      case e: Exception =>
        println("EXCEPTION:")
        throw e
    }
  }

  def constructOptimizedTree(): TreeNode = {
    null
  }
}
