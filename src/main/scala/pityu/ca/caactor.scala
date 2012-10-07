package pityu.ca.actors

import pityu.ca._

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Props
import akka.event.Logging

case class Coworker(actorRef: ActorRef, direction: Direction)
case class BorderFromCoworker[T](border: Array[Array[T]])
case object StartComputation

trait Direction
case object SouthWest extends Direction
case object South extends Direction
case object SouthEast extends Direction
case object West extends Direction
case object East extends Direction
case object NorthWest extends Direction
case object North extends Direction
case object NorthEast extends Direction

class CAActor[T, N <: Neighbourhood[T]](
    val maxSteps: Int,
    rule: (N) => T,
    neighbourhoodFactory: (Int, Int, Array[Array[T]]) => N,
    init: (Int, Int) => T,
    size: Int,
    border: Int)(implicit cm: ClassManifest[T]) extends Actor {

  val coworkers = scala.collection.mutable.ArrayBuffer[Tuple2[ActorRef, Direction]]()

  val borderTiles = scala.collection.mutable.Map[Direction, Array[Array[T]]]()

  val borderFunction = {
    (x1: Int, y1: Int, borderSize: Int, step: Int, mat: Array[Array[T]]) =>
      {
        val direction = (x1, y1) match {
          case (x, y) if x < borderSize && y < borderSize => NorthEast
          case (x, y) if x >= borderSize && x < borderSize + size && y < borderSize => North
          case (x, y) if x >= borderSize + size && y < borderSize => NorthWest
          case (x, y) if x < borderSize && y >= borderSize && y < borderSize + size => East
          case (x, y) if x >= borderSize + size && y >= borderSize && y < borderSize + size => West
          case (x, y) if x >= borderSize + size && y >= size + borderSize => SouthWest
          case (x, y) if x < borderSize && y >= size + borderSize => SouthEast
          case (x, y) if x >= borderSize && x < borderSize + size && y >= size + borderSize => South
        }
        val data: Array[Array[T]] = borderTiles(direction)
        direction match {
          case NorthEast => data(x1)(y1)
          case North => data(x1 - borderSize)(y1)
          case NorthWest => data(x1 - borderSize - size)(y1)
          case East => data(x1)(y1 - borderSize)
          case West => data(x1 - borderSize - size)(y1 - borderSize)
          case SouthEast => data(x1)(y1 - borderSize - size)
          case South => data(x1 - borderSize)(y1 - borderSize - size)
          case SouthWest => data(x1 - borderSize - size)(y1 - borderSize - size)
        }
      }
  }

  val ca = new CellularAutomaton[T, N](
    rule,
    neighbourhoodFactory,
    borderFunction,
    init,
    size,
    border
  )

  def readyForComputation = {
    borderTiles.size == 8
  }

  def makeStep {

    ca.makeStep

    borderTiles.clear
  }

  val log = Logging(context.system, this)
  def receive = {
    case Coworker(actor, direction) => {
      coworkers.append((actor, direction))
    }
    case BorderFromCoworker(data) => {
      val coworker = coworkers.find(x => x._1 == sender).get
      val direction = coworker._2
      borderTiles.update(direction, data.asInstanceOf[Array[Array[T]]])
      if (readyForComputation) {
        makeStep
      }
    }
    case x ⇒ log.info("received unknown message: " + x)
  }
}