package pityu.ca

object Moore {
  case class MooreNeighbourhood[@specialized(Double, Int) T](nw: T, n: T, ne: T, w: T, c: T, e: T, sw: T, s: T, se: T)
      extends Neighbourhood[T] {
    def sum(f: (T, T) => Int) = f(ne, nw) + f(se, sw) + f(n, s) + f(e, w)
  }

  def factory[@specialized(Double, Int) T](x: Int, y: Int, mat: Array[Array[T]]): MooreNeighbourhood[T] = MooreNeighbourhood(
    nw = mat(x - 1)(y - 1),
    n = mat(x)(y - 1),
    ne = mat(x + 1)(y - 1),
    w = mat(x - 1)(y),
    c = mat(x)(y),
    e = mat(x + 1)(y),
    sw = mat(x - 1)(y + 1),
    s = mat(x)(y + 1),
    se = mat(x + 1)(y + 1)
  )

}