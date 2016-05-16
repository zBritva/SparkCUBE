package org.zbritva.graph


//Class for convert CUBE tree to input format for simplex method
class BiparteGrapthMatching {
  def solve(edges_cost: Array[Array[Int]]) {
    // expand souece matrix with fictive vertex's (start and finish vertexts)
    // var cols_count = edges_cost.length;
    //		val test = Array(
    //			Array(1, 2, Int.MaxValue),
    //			Array(Int.MaxValue, 1, 2),
    //			Array(1, Int.MaxValue, 2)
    //		)
    val edges_cost_ext = extend_matrix(edges_cost)



    val value = objective_function(edges_cost_ext)

    print(value)
  }

  def extend_matrix(edges_cost: Array[Array[Int]]) : Array[Array[Int]] = {
    //create new array with length of row. It is a pseudo row
    val extension = new Array[Int](edges_cost(0).length)

    //init values with zeros
    //maybe it is not need
    for( index <- extension.indices){
      extension(index) = 1
    }

    //extend source matrix with new row
    var edges_cost_ext = edges_cost :+ extension

    //add psuedo colum
    //foreach
    for( index <- edges_cost_ext.indices){
      //add item in end of each row for create new column
      edges_cost_ext(index) = edges_cost_ext(index) :+ 1
    }

    edges_cost_ext
  }

  def objective_function(edges_cost: Array[Array[Int]]) : Int = {
    val objective_function_nodes = edges_cost(edges_cost.length - 1).length - 1
    var objective_function_value = 0

    for( index <- edges_cost(objective_function_nodes).indices){
      val value = edges_cost(objective_function_nodes)(index)
      objective_function_value = objective_function_value + value
    }

    objective_function_value
  }
}

