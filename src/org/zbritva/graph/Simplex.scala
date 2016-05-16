/**
  * Created by iigaliev on 16.05.2016.
  */

package org.zbritva.graph

import scala.util.control.Breaks._
import scala.collection.immutable.List

//Simple class for solving optimization problem task
class Simplex(source: Array[Array[Int]]) {
  var m: Int = source.length
  var n: Int = source(0).length

  var table: Array[Array[Int]] = Array.fill(m, n + m - 1) {
    0
  }

  var basis: List[Int] = List[Int](0)

  for (row <- table.indices) {
    for (col <- table(row).indices) {
      if (col < n)
        table(row)(col) = source(row)(col)
      else
        table(row)(col) = 0

      if ((n + row) < table(0).length) {
        table(row)(n + row) = 1
        basis.::(n + row)
      }
    }
  }

  n = table(0).length

  def Calculate(): Array[Array[Int]] = {
    var mainCol: Int = 0
    var mainRow: Int = 0

    val result: Array[Int] = Array.fill(1) {
      0
    }

    while (isEnd()) {
      mainCol = findMainCol()
      mainRow = findMainRow(mainCol)

      basis = basis.updated(mainRow, mainCol)

      val new_table: Array[Array[Int]] = Array.fill(m, n) {
        0
      }

      for (j <- Range(0, n)) {
        new_table(mainRow)(j) = table(mainRow)(j) / table(mainRow)(mainCol)
      }

      for (i <- Range(0, m)) {

        for (j <- Range(0, n)) {
          new_table(i)(j) = new_table(i)(j) - new_table(i)(mainCol) * new_table(mainRow)(j)
        }
      }
      table = new_table
    }

    for (i <- result.indices) {
      val k = basis.apply(i + 1)
      if (k != -1)
        result(i) = table(k)(0)
      else
        result(i) = 0
    }

    table
  }

  def isEnd(): Boolean = {
    var flag = true

    breakable {
      for (j <- Range(1, n)) {
        if (table(m - 1)(j) < 0) {
          flag = false
          break
        }
      }
    }

    flag
  }

  def findMainCol(): Int = {
    var mainCol = 1
    for (j <- Range(2, n)) {
      if (table(m - 1)(j) < table(m - 1)(mainCol))
        mainCol = j
    }

    mainCol
  }

  def findMainRow(mainCol: Int): Int = {
    var mainRow = 0

    breakable {
      for (i <- Range(0, m - 1)) {
        if (table(i)(mainCol) > 0) {
          mainRow = i
          break
        }
      }
    }

    for (i <- Range(mainRow + 1, m - 1)) {
      if ((table(i)(mainCol) > 0) &
        (table(i)(0) / table(i)(mainCol)) < (table(mainRow)(0) / table(mainRow)(mainCol))) {
        mainRow = i
      }
    }

    mainRow
  }
}
