package org.zbritva.main

/**
  * Created by zBritva on 04.05.16.
  */

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.log4j.{Level, Logger}
import org.apache.spark.rdd.RDD
import org.zbritva.cube.NaiveCubing
import org.zbritva.graph.BiparteGrapthMatching

object SparkCubeRun extends App {


  override def main(args: Array[String]) {
    val conf = new SparkConf()
    val sc = new SparkContext(master = "spark://192.168.142.176:7077", appName = "SparkCubeRun", conf)

    //test spark
    val data = Array(1, 2, 3, 4, 5)
    val distData = sc.parallelize(data)

    val result = distData.count()
    print(result)


//    val naive = new NaiveCubing(distData.asInstanceOf[RDD])
//    naive.execute( List("1", "2", "3") , "sum")

//    val graph_sover = new BiparteGrapthMatching()
//
//    Array(1.9, 2.9, 3.4, 3.5)
//
//    val test = Array(
//      Array(1, 2, Int.MaxValue),
//      Array(Int.MaxValue, 1, 2),
//      Array(1, Int.MaxValue, 2)
//    )
//
//    graph_sover.solve(test)

  }
}
