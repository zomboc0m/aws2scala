package com.monsanto.arch.awsutil.config.model


/**
  * SourceDetail specifies the kinds of events that can trigger the evaluation of the config rule
  *
  * @param eventSource The source of the event.  At this time, aws.config is the only valid option
  * @param maximumExecutionFrequency How often the source will trigger an evaluation.  Note: if maximumExecutionFrequency
  *                                  is set here, messageType MUST be ScheduledNotification
  * @param messageType defines what kind of message will trigger an evaluation.  See [[MessageType]] for details
  */
case class SourceDetail(eventSource: Option[EventSource], maximumExecutionFrequency: Option[ExecutionFrequency], messageType: Option[MessageType])

//maybe extract later
sealed abstract class EventSource(val name: String){override def toString = name}

object EventSource {

  case object AwsConfig extends EventSource("aws.config")

  val values: Seq[EventSource] = Seq(AwsConfig)

  def apply(str: String): EventSource =
    unapply(str).getOrElse(throw new IllegalArgumentException(s"‘$str’ is not a valid Event Source"))

  def unapply(str: String): Option[EventSource] =
    values.find(_.name == str)

}
