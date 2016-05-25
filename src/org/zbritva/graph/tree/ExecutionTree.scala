package org.zbritva.graph.tree

//import javax.swing.tree.TreeNode

//import org.zbritva.graph.CubeTree

/**
  * Created by iigaliev on 20.05.2016.
  */
class ExecutionTree() {

//  var root: TreeNode = _
//
//  def setCubeTree(cubetree: CubeTree){
//    var root_candidate = new TreeNode()
//    var current_node: TreeNode = null
//    var parent_node: TreeNode = null
//
//    val tree_levels = cubetree.getTree
//
//    //foreach map
//    for((level, nodes) <- tree_levels){
//        //foreach set
//        for (node <- nodes) {
//          if (level == 0) {
//            //process root node
//            parent_node = new TreeNode()
//            parent_node.setNodeColumns(node)
//            root = parent_node
//          }
//          else{
//            current_node = new TreeNode()
//            //find parent
//            //foreach all nodes from parent level
//            val parent_level = level - 1
//            for (parent_level_node <- tree_levels(parent_level)){
//              val parent_level_node_set = parent_level_node.toSet
//              var current_node_set = node.toSet
//              var intersection = parent_level_node_set.intersect(current_node_set)
//
//              //if current node columns contains in colums list of node from parent level
//              //that means we found parent -> child relation
//              if(intersection.size == node.length){
//
//              }
//            }
//          }
//        }
//    }
//
//  }
//
//  def getRoot() : TreeNode = {
//    root
//  }
//
//  def optimize(): Unit ={
//    //TODO get statistics from Spark to optimize execution plan
//  }
//
//  def constructSimpexMethodTask(): Array[Array[Double]]={
//    //TODO implement converter
//    val table: Array[Array[Double]]
//      = Array(
//      Array(25, -3, 5),
//      Array(30, -2, 5),
//      Array(10, 1, 0),
//      Array(6, 3, -8),
//      Array(0, -6, -5))
//
//    table
//  }

}
