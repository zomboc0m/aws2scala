package com.monsanto.arch.awsutil.config.model

/**
  * StartConfigRuleEvaluationRequest instructs AWS to begin evaluating one or more config rules
  * @param configRuleNames The names of the config rules to evaluate
  */
case class StartConfigRuleEvaluationRequest(configRuleNames: Seq[String])
