package com.monsanto.arch.awsutil.config

import akka.NotUsed
import akka.stream.scaladsl.Flow
import com.monsanto.arch.awsutil.StreamingAwsClient
import com.monsanto.arch.awsutil.config.model.{ConfigRule, PutRuleRequest}

trait StreamingConfigClient extends StreamingAwsClient {
  /** Returns a flow that creates a new config rule and emits the rule */
  def rulePutter: Flow[PutRuleRequest, ConfigRule, NotUsed]

  /** Returns a flow that given a rule name will delete it and emit the name. */
  def ruleDeleter: Flow[String, String, NotUsed]
}
