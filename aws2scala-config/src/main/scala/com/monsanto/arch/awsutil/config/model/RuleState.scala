package com.monsanto.arch.awsutil.config.model

/**
  * RuleState defines the current state of the config rule and whether it is in use.
  */
sealed abstract class RuleState(val name: String) {
  override def toString = name
}

object RuleState {
  case object Active extends RuleState("ACTIVE")

  case object Deleting extends RuleState("DELETING")

  case object DeletingResults extends RuleState("DELETING_RESULTS")

  case object Evaluating extends RuleState("EVALUATING")

  val values = Seq(Active, Deleting, DeletingResults, Evaluating)

  def apply(str: String): RuleState = unapply(str).getOrElse(throw new IllegalArgumentException(s"‘$str’ is not a valid rule state."))

  def unapply(str: String): Option[RuleState] = values.find(_.name == str)

}
