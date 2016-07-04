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

  val source_inversed = invertConditionTask(source)
//  val source_inversed = source

  var simplex_table: Array[Array[Double]] = Array.fill(row_count, col_count + row_count - 1) {
    0
  }

  //index - is row, value is column
  var basis: List[Int] = List[Int]()

  for (row <- simplex_table.indices) {
    for (col <- simplex_table(row).indices) {
      if (col < col_count)
        simplex_table(row)(col) = source_inversed(row)(col)
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

  def Calculate(): Tuple2[Array[Array[Double]], Array[Double]] = {
    var mainCol: Int = 0
    var mainRow: Int = 0

    var result: Array[Double] = Array.fill(solution_length) {
      0
    }

    var firstStage: Boolean = true

    var integerSolution: Boolean = false

    while (!integerSolution) {
      while (!isEnd() || firstStage) {
        firstStage = false
        mainCol = findMainCol()
        mainRow = findMainRow(mainCol)

        //exchange variables in main column and main row
        //меняем переменные лещажие на главной строке и столбце меняются местами
        basis = basis.updated(mainRow, mainCol)

        val new_table: Array[Array[Double]] = Array.fill(row_count, col_count) {
          0
        }

        //recalculate simplex table
        //расчет симплекс таблицы

        //divide elements on main column to main element
        //элементы ведущей строки, разделяем на главный элемент
        for (col <- Range(0, col_count)) {
          new_table(mainRow)(col) = simplex_table(mainRow)(col) / simplex_table(mainRow)(mainCol)
        }

        //полученные элементы умножаем на главный элемент и складываем элементом строки
        //given result multiply to minus main element and add to current element value (because minus main element- we are just subtract it)
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

      integerSolution = isIntegerSolution(result)
      if(!integerSolution) {
        addConstraint(result)
        //expand result lenght, because we added new constraint variable
        result = result :+ 0.0
        //because we added new variable, need one step of simplex method
        //Так как добавили новую переменную, и получили новую задачу, который нужно решить
        firstStage = true
      }
      else{
        for (index <- result.indices) {
          result(index) = Math.round(result(index))
        }
      }

      for (index <- result.indices) {
        print(result(index))
        print(", ")
      }
      println("ITERATION")
    }

    (simplex_table, result)

  }

  def isIntegerSolution(result: Array[Double]): Boolean ={
    val epsilon = 0.001
    for(solution <- result.indices){
      if(Math.abs(result(solution) - Math.round(result(solution))) > epsilon){
        return false
      }
    }

    true
  }

  def addConstraint(result: Array[Double]): Unit ={
    //TODO expand result
    //find integer parts of solutions
    val integer_part: Array[Int] = Array.fill(result.length) {
      0
    }

    var maxFractionIndex = 0
    for(index <- result.indices){
      val k = basis.indexOf(index + 1)
      if (k != -1) {
        integer_part(index) = result(index).toInt
        if (result(index) - integer_part(index) > result(maxFractionIndex).toInt - integer_part(maxFractionIndex)) {
          maxFractionIndex = index + 1
        }
      }
    }

    //находим наибольшую дробную часть среди базисных решений
    val maxFractionBasisIndex = basis.indexOf(maxFractionIndex)

    //add constraint to basis
    val constraint: Array[Double] = Array.fill(simplex_table(0).length) {
      0
    }

    //copy row with max fraction value with opposite sign
    for(index <- constraint.indices){
      constraint(index) = -1 * simplex_table(maxFractionBasisIndex)(index)
    }

    //current plan for new constraint is max fraction of current optimal plan
    constraint(0) = result(maxFractionIndex - 1).toInt - simplex_table(maxFractionBasisIndex)(0)
    constraint(maxFractionIndex) = 0

    //дополнительное ограничение строится правильно
    simplex_table = simplex_table ++ Array(constraint)

    //add columns of variable
    for(index <- simplex_table.indices){
      simplex_table(index) = simplex_table(index) :+ 0.0
    }
    col_count += 1
    row_count += 1

    simplex_table(simplex_table.length-1)(simplex_table(0).length - 1) = 1

    //exchange two last rows
    val first: Int = simplex_table.length - 1
    val second: Int = simplex_table.length - 2
    var buffer: Double = 0
    for (index <- simplex_table(0).indices){
      buffer = simplex_table(first)(index)
      simplex_table(first)(index) = simplex_table(second)(index)
      simplex_table(second)(index) = buffer
    }

    //TODO добавить в базис
    basis = basis ::: List(simplex_table(0).length - 1)
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

  //find main column: max element by ABS among negative coefficients of variable of function value
  def findMainCol(): Int = {
    var mainCol = 1

    //Находим первую попавшуюся не базисную переменную
    breakable {
      for (col <- Range(1, col_count)) {
        if (basis.indexOf(col) == -1) {
          mainCol = col
          break()
        }
      }
    }
    for (col <- Range(1, col_count)) {
      if (simplex_table(row_count - 1)(col) < simplex_table(row_count - 1)(mainCol) && basis.indexOf(col) == -1) //basis.indexOf(col) == -1 - among not basis variables
          mainCol = col
    }

    mainCol
  }

  //find variable for removing from basis
  //
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

  def invertConditionTask(conditions:  Array[Array[Double]]): Array[Array[Double]] = {
    var max = conditions(row_count - 1)(0)
    for (col <- Range(0, col_count - 1)) {
      if (Math.abs(max) < Math.abs(conditions(row_count - 1)(col))) {
        max = conditions(row_count - 1)(col)
      }
    }

    val sign = max / Math.abs(max)

    for (col <- Range(1, conditions(0).length)) {
      conditions(row_count - 1)(col) = sign * (Math.abs(max) - Math.abs(conditions(row_count - 1)(col)))
    }

    conditions
  }
}
