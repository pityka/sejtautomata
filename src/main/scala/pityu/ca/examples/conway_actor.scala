// package pityu.ca.examples

// import pityu.ca._
// import pityu.ca.actors._
// import Moore._

// import akka.actor.Actor
// import akka.actor.ActorSystem
// import akka.actor.Props
// import akka.event.Logging

// object RunConwayActor {
//   val size = 100
//   val border = 5
//   val steps = 100

//   val system = ActorSystem("MySystem")
//   val actor1 = system.actorOf(Props(new CAActor[Int, MooreNeighbourhood[Int]](
//     steps,
//     Conway.rule,
//     Moore.factory,
//     Conway.init,
//     size,
//     border
//   )), name = "actor1")
// }