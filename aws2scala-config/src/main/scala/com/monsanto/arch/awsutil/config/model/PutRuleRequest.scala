package com.monsanto.arch.awsutil.config.model

/**
  * PutRuleRequest represents a request to AWS to create a new config rule
  *
  * @param rule defines the rule to be created. See [[ConfigRule]] for details
  */
case class PutRuleRequest(rule: ConfigRule)
