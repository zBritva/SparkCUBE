/**
  * Created by iigaliev on 17.05.2016.
  */

package tests.org.zbritva.graph

import org.junit.Assert._
import org.junit.Test
import junit.framework.TestCase
import org.junit.Before
import org.zbritva.graph.Simplex

class SimplexTest extends TestCase {
  var simplex1: Simplex = _
  var simplex2: Simplex = _

  override def setUp: Unit = {

//  -3x1 + 5x2 <= 25
//  -2x1 + 5x2 <= 30
//    x1 <= 10
//   3x1 - 8x2 <= 6

//  f(x1,x2) = 6x1 + 5x2 => max

    val table1: Array[Array[Double]]
    = Array(
      Array(25, -3, 5),
      Array(30, -2, 5),
      Array(10, 1, 0),
      Array(6,  3, -8),
      Array(0, -6, -5))

    val table2: Array[Array[Double]]
    = Array(
      Array(4,  1, 1, 1),
      Array(10, 5, 2, 1),
      Array(0,  -5, -3, 0))

    simplex1 = new Simplex(table1)
    simplex2 = new Simplex(table2)
  }

  def testCalculation: Unit = {
    val result1 = simplex1.Calculate()
    val simplex_table1 = result1._1
    val values1 = result1._2
    val values_check1 = Array[Double](10, 10)
    assertArrayEquals("Check simplex method", values1, values_check1, 0.001)

    val result2 = simplex2.Calculate()
    val simplex_table2 = result2._1
    val values2 = result2._2
    val values_check2 = Array[Double](2.0/3.0, 10.0/3, 0)
    assertArrayEquals("Check simplex method", values2, values_check2, 0.001)
  }
}