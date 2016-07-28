package org.zbritva.graph.tree

import scala.collection.mutable.ListBuffer

/**
  * Created by iigaliev on 28.07.2016.
  */
class ExecutionPlanTree {
  var name: String = ""
  var order: Int = -1
  //first node always must be primary node (computing this child without sorting)
  var node_childs: ListBuffer[ExecutionPlanTree] = ListBuffer[ExecutionPlanTree]()

  def setName(name: String): Unit ={
    this.name = name
  }

  def getName(): String = {
    name
  }

  override def toString(): String= {
    name
  }

  def getPrimary(): ExecutionPlanTree = {
    node_childs = node_childs.sortBy(_.order)
    if(node_childs.nonEmpty){
      return node_childs.head
    }

    null
  }

  def getSecondary(order: Int): ExecutionPlanTree ={
    node_childs.filter(n=>n.order == order).head
  }

  def getChildCount(): Int = {
    node_childs.size
  }

  def addChild(node: ExecutionPlanTree): Unit ={
    node_childs.append(node)
  }

  def getSecondary(): List[ExecutionPlanTree] = {
    node_childs.filter(n=>n.order != 0).toList
  }

  def getPrimaryGroups(): List[String] = {

    val result = ListBuffer[String]()

    def rec(node: ExecutionPlanTree): Unit = {
      if (node != null){
        result.append(node.getName())
        rec(node.getPrimary())
      }
    }

    rec(this)

    result.toList
  }

}
