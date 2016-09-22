package com.monsanto.arch.awsutil.config

import akka.NotUsed
import akka.stream.scaladsl.Flow
import com.amazonaws.services.config.{AmazonConfigAsync, model => aws}
import com.monsanto.arch.awsutil.config.model.{ConfigRule, DescribeRulesRequest, PutRuleRequest}
import com.monsanto.arch.awsutil.{AWSFlow, AWSFlowAdapter}
import com.monsanto.arch.awsutil.converters.ConfigConverters._

import scala.collection.JavaConverters._

class DefaultStreamingConfigClient(config: AmazonConfigAsync) extends StreamingConfigClient {
  override val rulePutter =
    Flow[PutRuleRequest]
      .map(_.asAws)
      .via(AWSFlow.simple(AWSFlowAdapter.returnInput[aws.PutConfigRuleRequest, aws.PutConfigRuleResult](config.putConfigRuleAsync)))
      .map(_.getConfigRule.asScala)
      .named("Config.rulePutter")

  override val ruleDeleter =
    Flow[String]
      .map(n => new aws.DeleteConfigRuleRequest().withConfigRuleName(n))
      .via(AWSFlow.simple(AWSFlowAdapter.returnInput[aws.DeleteConfigRuleRequest, aws.DeleteConfigRuleResult](config.deleteConfigRuleAsync)))
      .map(_.getConfigRuleName)
      .named("Config.ruleDeleter")

  override val ruleDescriber =
    Flow[DescribeRulesRequest]
      .map(_.asAws)
      .via[aws.DescribeConfigRulesResult, NotUsed](AWSFlow.pagedByNextToken(config.describeConfigRulesAsync))
      .mapConcat(_.getConfigRules.asScala.toList.map(_.asScala))
      .named("Config.ruleLister")
}
