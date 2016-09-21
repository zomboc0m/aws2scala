package com.monsanto.arch.awsutil.config.model

import com.monsanto.arch.awsutil.{Account, Arn}
import com.monsanto.arch.awsutil.regions.Region

/** ConfigRuleArn represents the ARN of a Config Rule
  *
  * @param region  the region in which the rule resides
  * @param account the account that owns the rule
  * @param name    the name of the rule
  **/
case class ConfigRuleArn(region: Region, account: Account, name: String) extends Arn(Arn.Namespace.AwsConfig, Some(region), account){
  override def resource: String = s"config-rule:$name"
}

object ConfigRuleArn {

  /** Utility to build/extract `ConfigRuleArn` instances from strings. */
  object fromArnString {
    /** Builds a `ConfigRuleArn` object from the given ARN string. */
    def apply(arnString: String): ConfigRuleArn =
      unapply(arnString).getOrElse(throw new IllegalArgumentException(s"‘$arnString’ is not a valid rule ARN."))

    /** Extracts a `ConfigRuleArn` object from the given ARN string. */
    def unapply(arnString: String): Option[ConfigRuleArn] =
      arnString match {
        case Arn.fromArnString(ruleArn: ConfigRuleArn) ⇒ Some(ruleArn)
        case _ ⇒ None
      }
  }

  /** This partial function will be registered with the Arn superclass so that it can extract/build instances of its subclasses */
  private[awsutil] val configRuleArnPF: PartialFunction[Arn.ArnParts, ConfigRuleArn] = {
    case (_, Arn.Namespace.AwsConfig, Some(region), Some(account), RuleResourceRegex(name)) ⇒
      ConfigRuleArn(region, account, name)
  }

  private val RuleResourceRegex = "^config-rule:(.+)$".r
}
