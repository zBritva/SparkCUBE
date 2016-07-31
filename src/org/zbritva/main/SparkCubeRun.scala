package org.zbritva.main

/**
  * Created by zBritva on 04.05.16.
  */

import org.apache.spark.{SparkConf, SparkContext, SparkEnv}
import org.apache.log4j.{Level, Logger}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.Row
import org.zbritva.graph.{CubeTree, Simplex}
import tests.org.zbritva.graph.TestOptimizationTask


object SparkCubeRun extends App {

  case class Person(name: String, handedness: String, height: Double, weight: Double, avg: Double, HR: Int)

  override def main(args: Array[String]) {
    val conf = new SparkConf()
    conf.setJars(Seq("out\\artifacts\\CustomFunctions_jar\\CustomFunctions.jar"))
    //    val sc = new SparkContext(master = "spark://192.168.142.176:7077", appName = "SparkCubeRun", conf)
    val sc = new SparkContext(master = "local[4]", appName = "SparkCubeRun", conf)
    val sqlContext = new org.apache.spark.sql.SQLContext(sc)
    sc.addJar("out\\artifacts\\CustomFunctions_jar\\CustomFunctions.jar")
    import sqlContext.implicits._
    import org.zbritva.rdd.DataFrameCubeExtension._

    //    val dataAndHeader = sc.textFile("C:\\SparkData\\baseball_data.csv")
    val dataAndHeader = sc.textFile("D:\\SparkData\\baseball_data.csv")

    // split / clean data
    val headerString = dataAndHeader.first()
    val header = headerString.split(",")
    //    val data = dataAndHeader.filter(str => org.zbritva.udf.CustomFunctions.notEqual(str, headerString))
    //    print(data.count())

    val dataAndHeader_df = dataAndHeader.filter(r => r != "name,handedness,height,weight,avg,HR").map[Array[String]](_.split(",")).map[Person](
      p =>
        try {
          Person(
            p(0).trim,
            p(1).trim,
            p(2).trim.toDouble,
            p(3).trim.toDouble,
            p(4).trim.toDouble,
            p(5).trim.toInt)
        }
        catch {
          case e: Exception =>
            println("EXCEPTION:")
            throw e
        }
    ).toDF()
    dataAndHeader_df.cubePipeSort(
      dataAndHeader_df.col("height"),
      dataAndHeader_df.col("weight"),
      dataAndHeader_df.col("avg"),
      dataAndHeader_df.col("HR"))


    //test simplex method
    //    val table2: Array[Array[Double]]
    //    = Array(
    //      Array(21,  5,  7),
    //      Array(8,  -1,  3),
    //      Array(0,  -1, -2))
    //
    //    val simplex2 = new Simplex(table2)
    //    val result = simplex2.Calculate()
    //
    //    println(result)


    //list of columns of cubing
    //    val lst: List[String] = List(
    //      "A", "B", "C"
    //    )
    //
    //    val cubtree = new CubeTree(lst)
    //
    //    val tree = cubtree.getTree
    //
    //    new TestOptimizationTask().walkOnTree(tree.getRoot())
    //
    //    val task = tree._constructSimpexOptimizationTask()
    //
    //    println(task)
    //
    //    tree.solveOptimizationTask()
    //
    //    print("DONE")


  }
}
