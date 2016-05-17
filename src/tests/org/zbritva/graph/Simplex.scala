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
    val table: Array[Array[Int]]
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

    val values_check = Array[Int](10, 10)
  }
}