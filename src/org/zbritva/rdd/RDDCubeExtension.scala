package org.zbritva.rdd


import java.util.Calendar

import org.apache.commons.math3.geometry.euclidean.oned.Interval
import org.apache.spark.sql.{Column, DataFrame, GroupedData}
import org.zbritva.graph.CubeTree
import org.zbritva.graph.tree.{ExecutionTree, TreeNode}

import scala.collection.mutable.ListBuffer

/**
  * Created by iigaliev on 18.07.2016.
  */
object DataFrameCubeExtension {



  implicit class DataFrameCube(df: DataFrame) {

    val samples: DataFrame = df.sample(true, 0.1)

    def cubePipeSort(columns: Column*): org.apache.spark.sql.GroupedData ={
      //TODO CHECK cols (cols exists on DataFrame)

      val lst : scala.collection.mutable.ListBuffer[String] = ListBuffer[String]()

      for(col <- columns){
        lst.append(col.toString)
      }

      val cubtree = new CubeTree(lst.toList)
      val tree = cubtree.getTree

      val root = tree.getRoot()

      walkOnTree(root)

      tree._constructSimpexOptimizationTask()
      val plan = tree.solveOptimizationTask()

      executePlan(plan)
    }

    def walkOnTree(node: TreeNode): Unit = {
      if(node.node_columns.length == 1 && node.node_columns.head == "*")
        return

      val cols = ListBuffer[Column]()

      for(cl <- node.node_columns){
        cols.append(df.col(cl))
      }

      val start = System.currentTimeMillis()
      val cnt = samples.groupBy(cols:_*).count()
      val stop = System.currentTimeMillis()

      val delta = stop - start

      print("[")
      for(cl <- node.node_columns){
        print(cl)
        print(" ")
      }
      print("]")
      print("TIME: ")
      println(delta)

      node.setCostOfWitoutSorting(delta.toInt)
      node.setCostOfSorting(delta.toInt * 2) //for test

      //recursive walk to children
      if (node.getChildren() != null && node.getChildren().nonEmpty) {
        for (child <- node.getChildren()) {
          walkOnTree(child._3)
        }
      }
    }

    def executePlan(plan: Map[Int,List[(String, String)]]): GroupedData ={
      //TODO execute plan
      for(p <- plan.toList.sortBy(_._1)){
        //TODO find node for computing without sorting
        for(nodes <- p._2){

        }
      }

      df.cube()
    }

    def sclanRows(group: List[String]): Unit ={

    }
  }
}
