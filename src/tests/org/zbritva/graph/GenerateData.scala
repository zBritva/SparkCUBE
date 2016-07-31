package tests.org.zbritva.graph

import java.io._

/**
  * Created by iigaliev on 31.07.2016.
  */
object GenerateData extends App {
  var rand: scala.util.Random = _


  def getPrimaryHand(): String = {
    val r = rand.nextInt(3)

    r match {
      case 0 => "L"
      case 1 => "R"
      case 2 => "B"
      case _ => throw new Exception()
    }
  }

  override def main(args: Array[String]): Unit = {
    rand = new scala.util.Random()
    //    Joe Nolan,L, 71, 175, 0.263, 27
    //    Denny Doyle,L, 69, 175, 0.250, 16
    //    Jose Cardenal,R, 70, 150, 0.275, 138
    //    Mike Ryan,R, 74, 205, 0.193, 28
    //    Fritz Peterson,B, 72, 185, 0.159, 2

    val file = new File("D:\\SparkData\\baseball_data.csv")
    val bw = new BufferedWriter(new FileWriter(file))

    bw.write("name,handedness,height,weight,avg,HR" + sys.props("line.separator"))
    for (index <- Range(0, 1000 * 2000)) {
      val string = "NAME SURNAME, " + getPrimaryHand() + ", " + rand.nextInt(100) + ", " + rand.nextInt(200) + ", " + rand.nextFloat() + ", " + rand.nextInt(200) + sys.props("line.separator")
      bw.write(string)
    }

    bw.flush()
    bw.close()
  }
}
