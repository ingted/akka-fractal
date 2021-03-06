package fractal

import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

import akka.actor.ActorSystem

class Renderer(val actorSystem: ActorSystem) {
  this: RendererAlgorithm =>

  private implicit val dispatcher = actorSystem.dispatcher

  private def renderSegment(subtask: SubTask) = computeImageSegment(subtask)

  def render(numThreads: Int, task: Task): ImageSegment = {
    val subtasks = task.makeSubTasks(numThreads)
    val imageSegments = subtasks.map(subtask => Future(renderSegment(subtask)))
    val combinedSegments = Future.reduce(imageSegments)(_ + _)
    Await.result(combinedSegments, 10 seconds)
  }
}

trait RendererAlgorithm {
  protected def computeImageSegment(subtask: SubTask): ImageSegment
}