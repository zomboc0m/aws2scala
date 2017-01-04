package com.monsanto.arch.awsutil.config.model

sealed abstract class EventSource(val name: String){override def toString = name}

object EventSource {

  case object AwsConfig extends EventSource("aws.config")

  val values: Seq[EventSource] = Seq(AwsConfig)

  def apply(str: String): EventSource =
    unapply(str).getOrElse(throw new IllegalArgumentException(s"‘$str’ is not a valid Event Source"))

  def unapply(str: String): Option[EventSource] =
    values.find(_.name == str)
}
