package org.llm4s.agentic

import org.llm4s.llmconnect.LLMConnection
import com.azure.ai.openai.models._
import scala.collection.mutable.ListBuffer
import scala.concurrent.{Future, ExecutionContext}
import scala.jdk.CollectionConverters._
import scala.concurrent.ExecutionContext.Implicits.global

class AgenticRunner(llmConnection: LLMConnection) {

  private val client = llmConnection.client
  private val model  = llmConnection.defaultModel

  def runAgenticLoop(initialPrompt: String): Future[String] = {
    val chatMessages = new ListBuffer[AgentMessage]()
    chatMessages += SystemMessage(initialPrompt)
    chatMessages += UserMessage("Please write a Scala function to add two integers")

    def processLoop(messages: ListBuffer[AgentMessage]): Future[String] = {
      val chatCompletionsOptions = new ChatCompletionsOptions(
        messages.map {
          case SystemMessage(content)    => new ChatRequestSystemMessage(content)
          case UserMessage(content)      => new ChatRequestUserMessage(content)
          case AssistantMessage(content) => new ChatRequestAssistantMessage(content)
        }.asJava
      )

      val chatCompletions = client.getChatCompletions(model, chatCompletionsOptions)
      val choices = chatCompletions.getChoices.asScala

      if (choices.isEmpty) Future.successful("No response received.")

      val lastMessage = choices.head.getMessage.getContent
      println("LLM Response: " + lastMessage)

      messages += AssistantMessage(lastMessage)

      if (lastMessage.contains("[TOOL_CALL]")) {
        val toolName = extractToolName(lastMessage)
        ToolExecutor.executeTool(toolName).flatMap { toolResult =>
          messages += UserMessage(s"Tool Result: $toolResult")
          processLoop(messages)
        }
      } else {
        Future.successful(lastMessage)
      }
    }

    processLoop(chatMessages)
  }

  private def extractToolName(response: String): String = {
    // Extract tool name from response
    response.split("\\[TOOL_CALL\\]").lastOption.getOrElse("unknown_tool").trim
  }
}
