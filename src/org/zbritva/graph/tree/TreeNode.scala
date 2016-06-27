package org.zbritva.graph.tree


/**
  * Created by iigaliev on 20.05.2016.
  */
class TreeNode() {

  var node_columns: List[String] = List[String]()
  //list to childs with cost of computing
  //first cost of computing is if current node is properly sorted (wrong)
  //second cost of computing if current node is NOT! properly sorted (wrong)
  //TODO change two costes count according child of node,
  // for each child must be defined two costes one for if parent properly sorted for this child,
  // other if parent not properly sotded

  //costs are obsoleted
  var node_childs: List[(Int, Int, TreeNode)] = List[(Int, Int, TreeNode)]()

  //If current node is properly sorted
  var cost_with_sorting: Int = Int.MaxValue
  //if current node not properly sorted
  var cost_without_sorting: Int = Int.MinValue

  def getCostOfSorting(): Int = {
    cost_with_sorting
  }

  def getCostOfWitoutSorting(): Int = {
    cost_without_sorting
  }

  def setNodeColumns(columns: List[String]): Unit = {
    node_columns = columns.sorted(Ordering.String)
  }

  def getNodeColumns(): List[String] = {
    node_columns
  }

  def addChild(node: TreeNode, sortedcost: Int, unsortedcost: Int): Unit = {
    val relation = (sortedcost, unsortedcost, node)
    node_childs = node_childs.::(relation)
  }

  def addChild(node: TreeNode): Unit = {
    val relation = (Int.MaxValue, Int.MinValue, node)
    node_childs = node_childs.::(relation)
  }

  def getChildren(): List[(Int, Int, TreeNode)] = {
    node_childs
  }

  //  override def equals(o: Any) = super.equals(o)
  //  override def hashCode = super.hashCode

  override def hashCode: Int = {
    var strHash: String = ""
    for (columns <- node_columns.sorted(Ordering.String)) {
      strHash += columns
    }

    strHash.hashCode
  }

  override def equals(other: Any) = other match {
    case that: TreeNode =>
      var strHash: String = ""
      for (columns <- that.node_columns.sorted(Ordering.String)) {
        strHash += columns
      }
      var strHashOther: String = ""
      for (columns <- this.node_columns.sorted(Ordering.String)) {
        strHashOther += columns
      }

      strHash.equals(strHashOther)
    case _ => false
  }

  def checkColumns(columns: List[String]): Boolean = {
    val this_node_columns_set = getNodeColumns().toSet
    val current_node_set = columns.toSet
    var intersection = this_node_columns_set.intersect(current_node_set)

    if (intersection.size == columns.length && intersection.size == this_node_columns_set.size) {
      return true
    }

    false
  }
}
