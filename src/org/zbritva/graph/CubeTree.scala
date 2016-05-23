package org.zbritva.graph

import scala.List
import scala.collection.immutable
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Set

/**
  * Created by iigaliev on 20.05.2016.
  */


class CubeTree(columns: List[String]){

  val level_count = columns.size + 1
  var level_list: Map[Int,immutable.Set[List[String]]] = Map[Int,immutable.Set[List[String]]]()

  //firs level is list of columns
  level_list = level_list + (0 -> immutable.Set(columns))

  //generate items for each level
  //we are skiping zero level, because 0 level contain only one node and it is source colums list
  //we also skip last level, because it is special case: *
  for(level <- Range(1,level_count)) {
    //we must generate child nodes for all nodes in current level
    //so, process all nodes in current level
    val new_nodes = immutable.Set[List[String]]()

    var level_nodes = immutable.Set[List[String]]()
    level_list(level-1).foreach((node) => {
      //generate childs for each node in current level
      val new_nodes = getChildNodes(node)
      //add to set of exists nodes list
      level_nodes = level_nodes.++(new_nodes)
    })

    level_list = level_list + (level -> level_nodes)
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
    else{
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

  def getTree: Map[Int,scala.collection.immutable.Set[List[String]]] ={
    level_list
  }
}