package org.zbritva.main

/**
  * Created by zBritva on 04.05.16.
  */

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.log4j.{Level, Logger}
import org.apache.spark.rdd.RDD
import org.zbritva.cube.NaiveCubing
import org.zbritva.graph.{BiparteGrapthMatching, Simplex}

object SparkCubeRun extends App {


  override def main(args: Array[String]) {
//    val conf = new SparkConf()
//    val sc = new SparkContext(master = "spark://192.168.142.176:7077", appName = "SparkCubeRun", conf)
//
//    //test spark
//    val data = Array(1, 2, 3, 4, 5)
//    val distData = sc.parallelize(data)
//
//    val result = distData.count()
//    print(result)

    val table2: Array[Array[Double]]
    = Array(
      Array(21,  5,  7),
      Array(8,  -1,  3),
      Array(0,  -1, -2))

    val simplex2 = new Simplex(table2)
    val result = simplex2.Calculate()

    println(result)
  }
}
