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

    val table2: Array[Array[Double]]
    = Array(
      Array(21,  5,  7,  1,  0),
      Array(8,  -1,  3,  0,  1),
      Array(0,   -1,  -2,  0,  0))

    simplex2 = new Simplex(table2)
  }

  def testCalculation: Unit = {
    val result2 = simplex2.Calculate()
    val simplex_table2 = result2._1
    val values2 = result2._2
    val values_check2 = Array[Double](1, 2, 5, 0, 0, 0, 1)
    assertArrayEquals("Check simplex method", values2, values_check2, 0.001)
  }
}