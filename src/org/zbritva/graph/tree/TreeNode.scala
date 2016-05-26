package org.zbritva.graph.tree

/**
  * Created by iigaliev on 20.05.2016.
  */
class TreeNode() {

  var node_columns: List[String] = List[String]()
  //list to childs with cost of computing
  //first cost of computing is if current node is properly sorted
  //second cost of computing if current node is NOT! properly sorted
  var node_childs: List[(Int, Int, TreeNode)] = List[(Int, Int, TreeNode)]()

  def setNodeColumns(columns: List[String]): Unit ={
    node_columns = columns
  }

  def getNodeColumns(): List[String] ={
    node_columns
  }

  def addChild(node: TreeNode, sortedcost: Int, unsortedcost: Int): Unit ={
    val relation = (sortedcost, unsortedcost, node)
    node_childs = node_childs.::(relation)
  }

  def addChild(node: TreeNode): Unit ={
    val relation = (Int.MaxValue, Int.MaxValue, node)
    node_childs = node_childs.::(relation)
  }

  def getChilds(): List[(Int, Int, TreeNode)] = {
    node_childs
  }

//  override def equals(o: Any) = super.equals(o)
//  override def hashCode = super.hashCode

  override def hashCode: Int = {
    var strHash: String = ""
    for(columns <- node_columns.sorted(Ordering.String)){
      strHash += columns
    }

    strHash.hashCode
  }

  override def equals(other: Any) = other match {
    case that: TreeNode =>
      var strHash: String = ""
      for(columns <- that.node_columns.sorted(Ordering.String)){
        strHash += columns
      }
      var strHashOther: String = ""
      for(columns <- this.node_columns.sorted(Ordering.String)){
        strHashOther += columns
      }

      strHash.equals(strHashOther)
    case _ => false
  }
}
