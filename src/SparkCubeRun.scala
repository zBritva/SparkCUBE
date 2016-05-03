/**
  * Created by zBritva on 04.05.16.
  */

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext

object SparkCubeRun {
  def main(args: Array[String]) {
    val sc = new SparkContext(master = "local[*]", appName = "SparkCubeRun", new SparkConf())

    val data = Array(1, 2, 3, 4, 5)
    val distData = sc.parallelize(data)

    val result = distData.count()
    print(result)
  }
}
