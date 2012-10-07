package pityu.ca

object Borders {

  def toroid[@specialized(Double, Int) T](x: Int, y: Int, borderSize: Int, step: Int, mat: Array[Array[T]]): T = {
    val size = mat.length - 2 * borderSize
    // 1.asInstanceOf[T]
    mat(if (x < borderSize) (size + borderSize) - x else { if (x >= borderSize + size) x - size else x })(if (y < borderSize) (size + borderSize) - y else { if (y >= borderSize + size) y - size else y })
  }

  def uniform[@specialized(Double, Int) T](a1: T) = {
    (x: Int, y: Int, borderSize: Int, step: Int, mat: Array[Array[T]]) => a1
  }

}