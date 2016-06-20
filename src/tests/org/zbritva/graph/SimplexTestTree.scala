/**
  * Created by iigaliev on 17.05.2016.
  */

package tests.org.zbritva.graph

import junit.framework.TestCase
import org.junit.Assert._
import org.zbritva.graph.Simplex

class SimplexTestTree extends TestCase {
  var simplex1: Simplex = _

  override def setUp: Unit = {

//  -3x1 + 5x2 <= 25
//  -2x1 + 5x2 <= 30
//    x1 <= 10
//   3x1 - 8x2 <= 6

//  f(x1,x2) = 6x1 + 5x2 => max

    //Computing * from A or B or C or D
    val table1: Array[Array[Double]]
    = Array(
      Array(1, 1, 1, 1, 1),
      Array(0, 10, 25, 30, 40))

    simplex1 = new Simplex(table1)
  }

//  def testComputingFirstLevel: Unit = {
//    val result1 = simplex1.Calculate()
//    val simplex_table1 = result1._1
//    val values1 = result1._2
//    val values_check1 = Array[Double](1, 0)
//    assertArrayEquals("Check simplex method", values1, values_check1, 0.001)
//  }
}