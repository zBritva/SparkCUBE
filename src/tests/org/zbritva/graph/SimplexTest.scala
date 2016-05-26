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
  var simplex: Simplex = _

  override def setUp: Unit = {

//  -3x1 + 5x2 <= 25
//  -2x1 + 5x2 <= 30
//    x1 <= 10
//   3x1 - 8x2 <= 6

//  f(x1,x2) = 6x1 + 5x2 => max

    val table: Array[Array[Double]]
    = Array(
      Array(25, -3, 5),
      Array(30, -2, 5),
      Array(10, 1, 0),
      Array(6, 3, -8),
      Array(0, -6, -5))

    simplex = new Simplex(table)
  }

  def testCalculation: Unit = {
    val result = simplex.Calculate()
    val simplex_table = result._1
    val values = result._2

    val values_check = Array[Double](10, 10)

    assertArrayEquals("Check simplex method", values, values_check, 0.001)
  }
}