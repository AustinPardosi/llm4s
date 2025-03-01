package org.llm4s.agentic

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object ToolExecutor {

  def executeTool(toolCall: String): Future[String] = Future {
    println("Executing tool: " + toolCall)
    s"Result of $toolCall"
  }

}
