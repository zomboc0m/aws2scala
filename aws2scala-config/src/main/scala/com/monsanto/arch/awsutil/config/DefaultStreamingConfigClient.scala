package com.monsanto.arch.awsutil.config

import akka.NotUsed
import akka.stream.scaladsl.Flow
import com.amazonaws.services.config.{AmazonConfigAsync, model => aws}
import com.monsanto.arch.awsutil.config.model.PutRuleRequest
import com.monsanto.arch.awsutil.{AWSFlow, AWSFlowAdapter}
import com.monsanto.arch.awsutil.converters.ConfigConverters._

class DefaultStreamingConfigClient(config: AmazonConfigAsync) extends StreamingConfigClient {
  override val rulePutter =
    Flow[PutRuleRequest]
      .map(_.asAws)
      .via(AWSFlow.simple(AWSFlowAdapter.returnInput[aws.PutConfigRuleRequest,aws.PutConfigRuleResult](config.putConfigRuleAsync)))
      .map(_.getConfigRule.asScala)
      .named("Config.rulePutter")

  override def ruleDeleter: Flow[String, String, NotUsed] =
    Flow[String]
      .map(n => new aws.DeleteConfigRuleRequest().withConfigRuleName(n))
      .via(AWSFlow.simple(AWSFlowAdapter.returnInput[aws.DeleteConfigRuleRequest,aws.DeleteConfigRuleResult](config.deleteConfigRuleAsync)))
      .map(_.getConfigRuleName)
      .named("Config.ruleDeleter")
}
