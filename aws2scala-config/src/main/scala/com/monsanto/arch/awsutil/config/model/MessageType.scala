package com.monsanto.arch.awsutil.config.model

/**
  * Message type defines the kinds of messages that can trigger an evaluation of a config rule
  */
sealed abstract class MessageType(val name: String) {
  override def toString = name
}

object MessageType {

  /** ConfigurationItemChangeNotification will trigger an evaluation when a resource specified in the rule's Scope is changed */
  case object ConfigurationItemChangeNotification extends MessageType("ConfigurationItemChangeNotification")

  /** ScheduledNotification messages are dispatched periodically at an interval defined by the user. See [[ExecutionFrequency]] */
  case object ScheduledNotification extends MessageType("ScheduledNotification")

  /** ConfigurationSnapshotDeliveryCompleted will trigger an evaluation when AWS Config delivers a configuration snapshot */
  case object ConfigurationSnapshotDeliveryCompleted extends MessageType("ConfigurationSnapshotDeliveryCompleted")


  val values: Seq[MessageType] = Seq(
    ConfigurationItemChangeNotification, ScheduledNotification, ConfigurationSnapshotDeliveryCompleted
  )

  def apply(str: String): MessageType =
    unapply(str).getOrElse(throw new IllegalArgumentException(s"‘$str’ is not a valid message type."))

  def unapply(str: String): Option[MessageType] =
    values.find(_.name == str)
}
