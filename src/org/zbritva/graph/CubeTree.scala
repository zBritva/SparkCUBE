package org.zbritva.graph

import scala.collection.{immutable, mutable}
import scala.collection.mutable.ListBuffer
import org.zbritva.graph.tree.TreeNode
import org.zbritva
import org.zbritva.graph.tree.ExecutionTree

import scala.util.control.Breaks._

/**
  * Created by iigaliev on 20.05.2016.
  */


class CubeTree(columns: List[String]) {

  var root = new TreeNode()
  root.setNodeColumns(columns)

  val level_count = columns.size + 1
  var level_list: Map[Int, immutable.Set[List[String]]] = Map[Int, immutable.Set[List[String]]]()
  var level_list_tree: Map[Int, mutable.Set[TreeNode]] = Map[Int, mutable.Set[TreeNode]]()

  var all_nodes: List[TreeNode] = List[TreeNode]()
  this.all_nodes = this.all_nodes.::(root)

  //last level is list of columns
  level_list = level_list + (level_count - 1 -> immutable.Set(columns))

  //generate items for each level
  //we are skiping zero level, because 0 level contain only one node and it is source colums list
  //we also skip last level, because it is special case: *
  for (level <- Range(0, level_count - 1).reverse) {
    //we must generate child nodes for all nodes in current level
    //so, process all nodes in current level
    //val new_nodes = immutable.Set[List[String]]()

    var level_nodes = immutable.Set[List[String]]()
    //TODO forach childs of parent element
    level_list(level + 1).foreach((node) => {
      //generate childs for each node in current level
      val parent = getNode(node)

      if (level == 0) {
        val node_cols: List[String] = immutable.List[String] {
          "*"
        }
        var child = getNode(node_cols)
        if(child == null) {
          child = new TreeNode()
          child.setNodeColumns(node_cols)
          this.all_nodes = this.all_nodes.::(child)
        }
        parent.addChild(child)
        var zero_level_nodes = immutable.Set[List[String]]()
        zero_level_nodes = zero_level_nodes.+(node_cols)
        level_nodes = level_nodes.++(zero_level_nodes)
      }
      else {
        val new_nodes = getChildNodes(node)

        //create node elements for childs
        for (nd <- new_nodes) {
          val child = new TreeNode()
          child.setNodeColumns(nd)
          this.all_nodes = this.all_nodes.::(child)
          parent.addChild(child)
        }
        //add to set of exists nodes list
        level_nodes = level_nodes.++(new_nodes)
      }
    })

    level_list = level_list + (level -> level_nodes)
  }

  _recursiveWalkAcrossTree(root, level_count - 1)

  def _recursiveWalkAcrossTree(node: TreeNode, currentLevel: Int): Unit = {
    //  Map[Int, mutable.Set[TreeNode]]
    var set: mutable.Set[TreeNode] = null

    if (!(level_list_tree contains currentLevel)) {
      set = mutable.Set[TreeNode]()
      level_list_tree = level_list_tree.+(currentLevel -> set)
    }
    level_list_tree = level_list_tree.updated(currentLevel, level_list_tree(currentLevel) + node)
    if (currentLevel == 0)
      return
    for (child <- node.getChilds()) {
      _recursiveWalkAcrossTree(child._3, currentLevel - 1)
    }
  }

  def getNode(columns: List[String]): TreeNode = {
    var result: TreeNode = null
    breakable {
      for (node <- this.all_nodes) {
        val parent_level_node_set = node.getNodeColumns().toSet
        val current_node_set = columns.toSet
        var intersection = parent_level_node_set.intersect(current_node_set)

        if (intersection.size == columns.length && intersection.size == parent_level_node_set.size) {
          result = node
          break()
        }
      }
    }
    result
  }

  //This method redurning a set of child nodes for current node
  def getChildNodes(node: List[String]): immutable.Set[List[String]] = {
    var new_nodes: immutable.Set[List[String]] = scala.collection.immutable.Set[List[String]]()
    //TODO: generate childs for node

    //special case (generating nodes for last level)
    if (node.size == 1) {
      new_nodes = new_nodes + immutable.List[String] {
        "*"
      }
    }
    //generating nodes for other levels
    else {
      node.foreach((column_name) => {
        //copy node colums
        var new_node = ListBuffer[String]()
        new_node.appendAll(node)
        new_node.remove(new_node.indexOf(column_name))
        new_nodes = new_nodes.+(new_node.toList)
      })
    }

    new_nodes
  }

  def getTree: ExecutionTree = {
    new ExecutionTree(root, level_list, level_list_tree)
  }

}