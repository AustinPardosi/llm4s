package org.llm4s.agentic

sealed trait AgentMessage {
  def content: String
}

case class SystemMessage(content: String) extends AgentMessage
case class UserMessage(content: String) extends AgentMessage
case class AssistantMessage(content: String) extends AgentMessage
