package pityu.ca.examples

import pityu.ca._
import Moore._

object Helpers {
  def print[T](mat: Array[Array[T]]): String = {
    (for (j <- 0 until mat.length) yield {
      (for (i <- 0 until mat.length) yield {
        // "(" + i + "," + j + "):" + 
        mat(i)(j).toString
      }).mkString(" ")
    }).mkString("\n")
  }
}

object Conway {
  val rule: (MooreNeighbourhood[Int] => Int) = { n: MooreNeighbourhood[Int] =>
    val s = n.sum(_ + _)
    if (n.c == 1) {
      s match {
        case x if x < 2 => 0
        case x if x == 2 || x == 3 => 1
        case x if x > 3 => 0
      }
    } else {
      s match {
        case x if x == 3 => 1
        case _ => 0
      }
    }
  }
  val init: (Int, Int) => Int = (x: Int, y: Int) => (x, y) match {
    case (15, 15) => 1
    case (16, 16) => 1
    case (16, 17) => 1
    case (15, 17) => 1
    case (14, 17) => 1
    case _ => 0
  }
}

object RunConway extends App {
  val t = System.nanoTime
  val size = args(0).toInt
  val border = 4
  val threads = args(1).toInt
  val ca = new CellularAutomaton[Int, MooreNeighbourhood[Int]](
    Conway.rule,
    Moore.factory,
    Borders.uniform(0),
    Conway.init,
    size,
    border,
    threads
  )
  val window = new Window(size + 2 * border)
  (0 until 1000) foreach { x =>

    // println(Helpers.print(ca.state))
    // println("-")

    ca.makeStep

    if (ca.step % 1 == 0) {
      val mat: Array[Array[Int]] = ca.state.map { ar =>
        ar.map { v =>
          convertRGBToInt(255 * v, 255 * v, 255 * v, 255)
        }
      }
      window.setImage(mat)
    }

  }

  window.close
  println(((System.nanoTime - t) / 1000000000.0).toString + " seconds")
}