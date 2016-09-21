package com.monsanto.arch.awsutil.config.model

import com.monsanto.arch.awsutil.lambda.model.FunctionArn
import scala.util.Try

/**
  * SourceIdentifier identifies the lambda function that will preform the evaluation.  It can either
  * be an identifier that corresponds to a predefined rule managed by AWS or the ARN of a custom
  * lambda function
  */
sealed trait SourceIdentifier extends Product with Serializable {
  def name: String
}

case class ManagedSource(rule: ManagedRule) extends SourceIdentifier {
  override def name: String = rule.identifier

  override def toString = name
}

object ManagedSource {
  def apply(identifier: String): ManagedSource = ManagedSource(ManagedRule(identifier))

  def unapply(identifier: String): Option[ManagedRule] = ManagedRule.unapply(identifier)
}

case class LambdaSource(arn: FunctionArn) extends SourceIdentifier {
  override def name: String = arn.arnString

  override def toString = name
}

object LambdaSource {
  def apply(arnString: String): LambdaSource = LambdaSource(FunctionArn.fromArnString(arnString))
}


object SourceIdentifier {

  def apply(str: String): SourceIdentifier = {
    val t = Try(ManagedSource(str)) orElse Try(LambdaSource(str))
    t.getOrElse(throw new IllegalArgumentException(s"‘$str’ is not a valid source identifier."))
  }

  def apply(rule: ManagedRule): SourceIdentifier = ManagedSource(rule)

  def unapply(s: SourceIdentifier): Option[String] =
    Some(s.name)
}
