package pityu.ca

trait Neighbourhood[@specialized(Double, Int) T]

class CellularAutomaton[@specialized(Double, Int) T, N <: Neighbourhood[T]](
    rule: (N) => T,
    neighbourhoodFactory: (Int, Int, Array[Array[T]]) => N,
    getBorder: (Int, Int, Int, Int, Array[Array[T]]) => T,
    init: (Int, Int) => T,
    size: Int,
    border: Int,
    threads: Int)(implicit cm: ClassManifest[T]) {

  private val mat1: Array[Array[T]] = Array.tabulate(size + (2 * border), size + (2 * border))(init)

  private val mat2: Array[Array[T]] = Array.ofDim(size + (2 * border), size + (2 * border))

  private var stepCounter = 0

  def step = stepCounter

  private def getBackArray = if (stepCounter % 2 == 0) mat1 else mat2

  private def getWorkArray = if (stepCounter % 2 == 0) mat2 else mat1

  def state = getBackArray

  private lazy val borderIndices = scala.collection.mutable.ArrayBuffer[Tuple2[Int, Int]]()

  for (i <- 0 until size + 2 * border; j <- (0 until border) ++ ((size + border) until size + 2 * border)) {
    borderIndices.append((i, j))
  }
  for (i <- (0 until border) ++ ((size + border) until size + 2 * border); j <- border until size + border) {
    borderIndices.append((i, j))
  }

  lazy val coreIndices = (for (
      i <- border until size + border;
      j <- border until size + border
    ) yield (i, j)).par

  def makeStep = {
    val backArray = getBackArray
    val workArray = getWorkArray

    // Update border region
    borderIndices.foreach { tup =>
      workArray(tup._1)(tup._2) = getBorder(tup._1, tup._2, border, stepCounter, backArray)
    }

    // Update core region
    // var t = System.nanoTime
    val old = collection.parallel.ForkJoinTasks.defaultForkJoinPool.getParallelism
    collection.parallel.ForkJoinTasks.defaultForkJoinPool.setParallelism(threads)

    coreIndices.foreach { t =>
      val i = t._1
      val j = t._2
      workArray(i)(j) = rule(neighbourhoodFactory(i, j, backArray))
    }

    collection.parallel.ForkJoinTasks.defaultForkJoinPool.setParallelism(old)

    // (1 until threads).par.foreach { z =>
    //   var i = border + (size + border) / threads * (z - 1)
    //   var j = border
    //   while (i <= (size + border) / threads * z) {
    //     j = border
    //     while (j <= size + border) {
    //       workArray(i)(j) = rule(neighbourhoodFactory(i, j, backArray))
    //       j += 1
    //     }
    //     i += 1
    //   }
    // }

    // for (i <- border until size + border; j <- border until size + border) {
    //   workArray(i)(j) = rule(neighbourhoodFactory(i, j, backArray))
    // }
    // println(System.nanoTime - t)

    stepCounter += 1
  }

}