package org.llm4s

import org.llm4s.agentic.AgenticRunner
import org.llm4s.llmconnect.LLMConnect

import scala.concurrent.Await
import scala.concurrent.duration._

object Main {
  def main(args: Array[String]): Unit = {
    val llmConnection = LLMConnect.getClient()
    val agentRunner = new AgenticRunner(llmConnection)

    val result = Await.result(agentRunner.runAgenticLoop("You are a helpful assistant."), 60.seconds)
    println("Final Response: " + result)
  }
}
