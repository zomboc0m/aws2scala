package com.monsanto.arch.awsutil.config.model


/** Config Rule represents all the properties of an AWS Config Rule
  *
  * @param source Defines who owns the config rule, what is allowed to trigger its evaluation, and
  *               which lambda function preforms the evaluation. See [[Source]]
  * @param arn The arn of the config rule
  * @param id The id of the config rule
  * @param name The name of the config rule (required when adding a new rule)
  * @param description A user supplied description of the config rule
  * @param inputParameters A collection of parameters and their values that will be passed to the
  *                        lambda function when it is invoked
  * @param ruleState Indicates whether the config rule is active, evaluating, or being deleted
  * @param maximumExecutionFrequency The maximum frequency with which AWS Config runs evaluations for a rule
  * @param scope Defines what kinds of resources the config rule will evaluate
  */
case class ConfigRule(
                       source: Source,
                       arn: Option[ConfigRuleArn],
                       id: Option[String],
                       name: Option[String],
                       description: Option[String],
                       inputParameters: Option[Map[String, Any]],
                       ruleState: Option[RuleState],
                       maximumExecutionFrequency: Option[ExecutionFrequency],
                       scope: Option[Scope]
                     )

