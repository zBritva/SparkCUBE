/**
  * Created by iigaliev on 16.05.2016.
  */

package org.zbritva.graph

import scala.util.control.Breaks._
import scala.collection.immutable.List

//Simple class for solving optimization problem task
//TODO test solving optimization problem with equality constraints
class Simplex(source: Array[Array[Double]]) {
  var row_count: Int = source.length
  var col_count: Int = source(0).length
  var solution_length: Int = source(0).length - 1

  var simplex_table: Array[Array[Double]] = Array.fill(row_count, col_count + row_count - 1) {
    0
  }

  var basis: List[Int] = List[Int]()

  for (row <- simplex_table.indices) {
    for (col <- simplex_table(row).indices) {
      if (col < col_count)
        simplex_table(row)(col) = source(row)(col)
      else
        simplex_table(row)(col) = 0
    }
    if ((col_count + row) < simplex_table(0).length) {
      simplex_table(row)(col_count + row) = 1
      basis = basis.::(col_count + row)
    }
  }

  basis = basis.reverse

  col_count = simplex_table(0).length

  def Calculate(): Tuple2[Array[Array[Double]],Array[Double]]  = {
    var mainCol: Int = 0
    var mainRow: Int = 0

    val result: Array[Double] = Array.fill(solution_length) {
      0
    }

    var firstStage: Boolean = false

    while (!isEnd() || firstStage) {
      firstStage = false
      mainCol = findMainCol()
      mainRow = findMainRow(mainCol)

      basis = basis.updated(mainRow, mainCol)

      val new_table: Array[Array[Double]] = Array.fill(row_count, col_count) {
        0
      }

      for (col <- Range(0, col_count)) {
        new_table(mainRow)(col) = simplex_table(mainRow)(col) / simplex_table(mainRow)(mainCol)
      }

      for (row <- Range(0, row_count)) {
        if (row != mainRow) {
          for (col <- Range(0, col_count)) {
            new_table(row)(col) = simplex_table(row)(col) - simplex_table(row)(mainCol) * new_table(mainRow)(col)
          }
        }
      }
      simplex_table = new_table
    }

    for (index <- result.indices) {
      val k = basis.indexOf(index + 1)
      if (k != -1)
        result(index) = simplex_table(k)(0)
      else
        result(index) = 0
    }

    (simplex_table, result)

  }

  def isEnd(): Boolean = {
    var flag = true

    breakable {
      for (col <- Range(1, col_count)) {
        if (simplex_table(row_count - 1)(col) < 0) {
          flag = false
          break
        }
      }
    }

    flag
  }

  def findMainCol(): Int = {
    var mainCol = 1
    for (col <- Range(2, col_count)) {
      if (simplex_table(row_count - 1)(col) < simplex_table(row_count - 1)(mainCol))
        mainCol = col
    }

    mainCol
  }

  def findMainRow(mainCol: Int): Int = {
    var mainRow = 0

    breakable {
      for (row <- Range(0, row_count - 1)) {
        if (simplex_table(row)(mainCol) > 0) {
          mainRow = row
          break
        }
      }
    }

    for (row <- Range(mainRow + 1, row_count - 1)) {
      if ((simplex_table(row)(mainCol) > 0) &
        (simplex_table(row)(0) / simplex_table(row)(mainCol)) < (simplex_table(mainRow)(0) / simplex_table(mainRow)(mainCol))) {
        mainRow = row
      }
    }

    mainRow
  }
}
