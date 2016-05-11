package org.zbritva.main

/**
* Created by zBritva on 04.05.16.
*/

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.log4j.{Level, Logger}

object SparkCubeRun extends App {


  override def main(args: Array[String]) {
    val conf = new SparkConf()
    val sc = new SparkContext(master = "local[*]", appName = "SparkCubeRun", conf)

    val data = Array(1, 2, 3, 4, 5)
    val distData = sc.parallelize(data)

    val result = distData.count()
    print(result)
  }
}
