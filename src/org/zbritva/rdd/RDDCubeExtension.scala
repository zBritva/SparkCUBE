package org.zbritva.rdd


import java.util.Calendar

import org.apache.commons.math3.geometry.euclidean.oned.Interval
import org.apache.spark.sql.{Column, DataFrame, GroupedData}
import org.zbritva.graph.CubeTree
import org.zbritva.graph.tree.{ExecutionPlanTree, ExecutionTree, TreeNode}

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

      val start = System.currentTimeMillis()
      walkOnTree(root)
      val stop = System.currentTimeMillis()

      val delta_naive = stop - start
      print("NAIVE1 ")
      println(delta_naive)


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
      val cnt = df.groupBy(cols:_*).count()
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


    def getSortSequence(plan: Map[Int,List[(String, String)]], level: Int, nodeColumns: String): List[String] ={
      val result = ListBuffer[String]()
      var searchColums = nodeColumns
      var searchLevel = level - 1
      result.append(searchColums)

      var matchedNode: List[(String, String)] = null
      do{
        val prevLevelNodes = plan(searchLevel)
        //find direct computing
        matchedNode =  prevLevelNodes.filter( str => str._1.trim == searchColums.trim)
        if(matchedNode.nonEmpty) {
          searchColums = matchedNode.head._2
          result.append(searchColums)
          searchLevel -= 1
        }
        //find resortComputing
      }
      while (matchedNode.nonEmpty)

      result.toList
    }

    def executePlan(plan: Map[Int,List[(String, String)]]): GroupedData ={
      val p = plan.toList.sortBy(_._1).reverse.head
      //TODO execute plan

      //TODO find node for computing without sorting

      if(false) {
        for (nodes <- p._2) {
          val parent = nodes._1
          val child = nodes._2

          val sort_col_names = parent.split(";").filter(s => s.trim != "")

          val cols: ListBuffer[Column] = ListBuffer[Column]()

          for (col <- sort_col_names) {
            val c = df.col(col.replace("'", ""))
            cols.append(c)
          }

          val sdf = df.sort(cols: _*)
          val sortSequence = getSortSequence(plan, p._1, child)
          println(sortSequence.toString)
        }

        //execute plan
        val physicalPlan = _testGetTree

        //List(height;weight;HR;avg, height;weight;HR;, height;weight;, *)
        print(physicalPlan.getPrimary().getPrimaryGroups().toString)

        for(index <- Range(1,physicalPlan.getChildCount())){
          //process primary
          if(index == 1) {
            var gr = physicalPlan.getPrimary().getPrimaryGroups()


          }
        }

      }

      var start = System.currentTimeMillis()
      df.sort(df.col("HR"), df.col("height"), df.col("avg"), df.col("weight")).collect()
      df.sort(df.col("avg"), df.col("height"), df.col("HR")).collect()

      df.sort(df.col("HR"), df.col("avg"), df.col("weight"), df.col("height")).collect()
      df.sort(df.col("HR"), df.col("weight"), df.col("avg")).collect()
      df.sort(df.col("weight"), df.col("avg"), df.col("HR")).collect()

      df.sort(df.col("height"), df.col("weight"), df.col("HR"), df.col("avg")).collect()
      df.sort(df.col("avg"), df.col("height"), df.col("weight"), df.col("HR")).collect()
      var stop = System.currentTimeMillis()
      val delta = stop - start
      print("PIPESORT ")
      println(delta) //12700

      start = System.currentTimeMillis()
      val cube = df.cube()
      stop = System.currentTimeMillis()

      val delta_naive = stop - start
      print("NAIVE2 ")
      println(delta_naive)

      cube
    }

    def sclanRows(group: List[String]): Unit ={

    }


    def _testGetTree(): ExecutionPlanTree ={
//      PHYSICAL PLAN EXAMPLE
//1      avg;height;weight;HR;''''''''' -> avg;height;weight
//2      height;weight;HR;avg;'''''' -> height;weight;HR -> height;weight -> height -> *
//3      HR;avg;weight;height''' -> HR;avg;weight -> HR;avg -> HR
//                                  HR;weight;avg'' -> HR;weight
//                                  weight;avg;HR;''' -> weight;avg -> weight
//4      HR;height;avg;;weight -> HR;height;avg -> HR;height
//                                avg;height;HR;'' -> avg;height -> avg
      val root = new ExecutionPlanTree()
      root.setName("_ROOT")

      //row 1
      if(true) {
        var all = new ExecutionPlanTree()
        all.setName("*")
        all.order = 0

        var hw = new ExecutionPlanTree()
        hw.setName("height;weight;")
        hw.order = 0

        var hwh = new ExecutionPlanTree()
        hwh.setName("height;weight;HR;")
        hwh.order = 0

        var hwha = new ExecutionPlanTree()
        hwha.setName("height;weight;HR;avg")
        hwha.order = 4

        hw.addChild(all)
        hwh.addChild(hw)
        hwha.addChild(hwh)
        root.addChild(hwha)
      }
      //row 2
      if(true) {
        var ahw = new ExecutionPlanTree()
        ahw.setName("avg;height;weight")
        ahw.order = 0

        var ahwh = new ExecutionPlanTree()
        ahwh.setName("avg;height;weight;HR")
        ahwh.order = 2

        ahwh.addChild(ahw)

        root.addChild(ahwh)
      }
      //row 3
      if(true){
        var h = new ExecutionPlanTree()
        h.setName("HR")
        h.order = 0

        var ha = new ExecutionPlanTree()
        ha.setName("HR;avg")
        ha.order = 0

        var haw = new ExecutionPlanTree()
        haw.setName("HR;avg;weight")
        haw.order = 0

        var hawh = new ExecutionPlanTree() //root
        hawh.setName("HR;avg;weight;height")
        hawh.order = 3

        var hw = new ExecutionPlanTree()
        h.setName("HR;weight")
        h.order = 0

        var hwa = new ExecutionPlanTree()
        hwa.setName("HR;weight;avg")
        hwa.order = 1

        //weight;avg;HR;''' -> weight;avg -> weight
        var w = new ExecutionPlanTree()
        w.setName("weight")
        w.order = 0

        var wa = new ExecutionPlanTree()
        wa.setName("weight;avg")
        wa.order = 0

        var wah = new ExecutionPlanTree()
        wah.setName("weight;avg;HR")
        wah.order = 2

        hawh.addChild(haw)
        hawh.addChild(hwa)
        hawh.addChild(wah)

        wa.addChild(w)
        wah.addChild(wa)

        ha.addChild(h)
        haw.addChild(ha)

        root.addChild(hawh)
      }
      //row 4
      if(true){
        var a = new ExecutionPlanTree()
        a.setName("avg")
        a.order = 0

        var ah = new ExecutionPlanTree()
        ah.setName("avg;height")
        ah.order = 0

        ah.addChild(a)

        var ahh = new ExecutionPlanTree()
        ahh.setName("avg;height;HR")
        ahh.order = 1

        ahh.addChild(ah)

        var hh = new ExecutionPlanTree()
        hh.setName("HR;height")
        hh.order = 1

        var hha = new ExecutionPlanTree()
        hha.setName("HR;height;avg")
        hha.order = 0

        hha.addChild(hh)

        var hhaw = new ExecutionPlanTree()
        hhaw.setName("HR;height;avg;;weight")
        hhaw.order = 1

        hhaw.addChild(hha)

        root.addChild(hhaw)
      }

      root
    }
  }
}
