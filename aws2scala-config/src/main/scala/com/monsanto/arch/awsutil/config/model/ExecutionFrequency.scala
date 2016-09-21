package com.monsanto.arch.awsutil.config.model

/** A set of predefined values specifying how often a lambda function is allowed to run */
sealed abstract class ExecutionFrequency(val name: String) {
  override def toString: String = name
}

object ExecutionFrequency {

  case object OneHour extends ExecutionFrequency("One_Hour")

  case object ThreeHours extends ExecutionFrequency("Three_Hours")

  case object SixHours extends ExecutionFrequency("Six_Hours")

  case object TwelveHours extends ExecutionFrequency("Twelve_Hours")

  case object TwentyFourHours extends ExecutionFrequency("TwentyFour_Hours")

  val values: Seq[ExecutionFrequency] = Seq(OneHour, ThreeHours, SixHours, TwelveHours, TwentyFourHours)

  def apply(name: String) = unapply(name).getOrElse(throw new IllegalArgumentException(s"‘$name’ is not a valid execution frequency for a config rule"))

  def unapply(str: String): Option[ExecutionFrequency] = values.find(_.name == str)
}
