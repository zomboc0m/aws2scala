package com.monsanto.arch.awsutil.converters

import com.amazonaws.services.config.{model => aws}
import com.monsanto.arch.awsutil.config.model._
import com.monsanto.arch.awsutil.config.model.DescribeRulesRequest
import collection.JavaConverters._
import org.json4s.native.Json
import org.json4s.DefaultFormats

/** Provides converters between ''aws2scala-config'' objects and their AWS Java SDK counterparts. */
object ConfigConverters {

  implicit class AwsConfigRule(val rule: aws.ConfigRule) extends AnyVal {
    def asScala: ConfigRule = ConfigRule(
      rule.getSource.asScala,
      Option(rule.getConfigRuleArn).map(ConfigRuleArn.fromArnString(_)),
      Option(rule.getConfigRuleId),
      Option(rule.getConfigRuleName),
      Option(rule.getDescription),
      Option(rule.getInputParameters).map(p => Json(DefaultFormats).read[Map[String, Any]](p)),
      Option(rule.getConfigRuleState).map(RuleState(_)),
      Option(rule.getMaximumExecutionFrequency).map(ExecutionFrequency(_)),
      Option(rule.getScope).map(_.asScala)
    )
  }

  implicit class ScalaConfigRule(val rule: ConfigRule) extends AnyVal {
    def asAws: aws.ConfigRule = {
      val a = new aws.ConfigRule().withSource(rule.source.asAws)
      rule.arn.foreach(x => a.withConfigRuleArn(x.arnString))
      rule.id.foreach(x => a.withConfigRuleId(x))
      rule.name.foreach(x => a.withConfigRuleName(x))
      rule.description.foreach(x => a.withDescription(x))
      rule.inputParameters.foreach(x => a.withInputParameters(Json(DefaultFormats).write(x)))
      rule.ruleState.foreach(x => a.withConfigRuleState(x.asAws))
      rule.maximumExecutionFrequency.foreach(x => a.withMaximumExecutionFrequency(x.asAws))
      rule.scope.foreach(x => a.withScope(x.asAws))
      a
    }
  }

  implicit class AwsPutRuleRequest(val ruleRequest: aws.PutConfigRuleRequest) extends AnyVal {
    def asScala: PutRuleRequest = PutRuleRequest(ruleRequest.getConfigRule.asScala)
  }

  implicit class ScalaPutRuleRequest(val ruleRequest: PutRuleRequest) extends AnyVal {
    def asAws: aws.PutConfigRuleRequest = new aws.PutConfigRuleRequest().withConfigRule(ruleRequest.rule.asAws)
  }

  implicit class AwsDescribeRulesRequest(val request: aws.DescribeConfigRulesRequest) extends AnyVal {
    def asScala: DescribeRulesRequest = DescribeRulesRequest(Option(request.getConfigRuleNames.asScala.toList))
  }

  implicit class ScalaDescribeRuleRequest(val request: DescribeRulesRequest) extends AnyVal {
    def asAws: aws.DescribeConfigRulesRequest = {
      val r = new aws.DescribeConfigRulesRequest()
      request.names.foreach(ns => r.withConfigRuleNames(ns:_*))
      r
    }
  }

  implicit class AwsSource(val source: aws.Source) extends AnyVal {
    def asScala: Source =
      Source(
        Owner.unapply(source.getOwner),
        Option(source.getSourceDetails).filter(!_.isEmpty).map(_.asScala.toList.map(_.asScala)),
        Option(source.getSourceIdentifier).map(SourceIdentifier(_))
      )
  }

  implicit class ScalaSource(val source: Source) extends AnyVal {
    def asAws: aws.Source = {
      val a = new aws.Source()
      source.owner.foreach(s => a.setOwner(s.asAws))
      source.sourceDetails.foreach(s => a.setSourceDetails(s.map(_.asAws).asJavaCollection))
      source.sourceIdentifier.foreach(s => a.setSourceIdentifier(s.name))
      a
    }
  }

  implicit class AwsSourceDetail(val detail: aws.SourceDetail) extends AnyVal {
    def asScala: SourceDetail =
      SourceDetail(
        EventSource.unapply(detail.getEventSource),
        ExecutionFrequency.unapply(detail.getMaximumExecutionFrequency),
        MessageType.unapply(detail.getMessageType)
      )
  }

  implicit class ScalaSourceDetail(val detail: SourceDetail) extends AnyVal {
    def asAws: aws.SourceDetail = {
      val a = new aws.SourceDetail()
      detail.eventSource.foreach(s => a.setEventSource(s.asAws))
      detail.maximumExecutionFrequency.foreach(e => a.setMaximumExecutionFrequency(e.asAws))
      detail.messageType.foreach(m => a.setMessageType(m.asAws))
      a
    }
  }

  implicit class AwsRuleState(val state: aws.ConfigRuleState) extends AnyVal {
    def asScala: RuleState = RuleState(state.toString)
  }

  implicit class ScalaRuleState(val state: RuleState) extends AnyVal {
    def asAws: aws.ConfigRuleState = aws.ConfigRuleState.fromValue(state.name)
  }


  implicit class AwsExecutionFrequency(val freq: aws.MaximumExecutionFrequency) extends AnyVal {
    def asScala: ExecutionFrequency = ExecutionFrequency(freq.toString)
  }

  implicit class ScalaExecutionFrequency(val freq: ExecutionFrequency) extends AnyVal {
    def asAws: aws.MaximumExecutionFrequency = aws.MaximumExecutionFrequency.fromValue(freq.name)
  }


  implicit class AwsMessageType(val message: aws.MessageType) extends AnyVal {
    def asScala: MessageType = MessageType(message.toString)
  }

  implicit class ScalaMessageType(val message: MessageType) extends AnyVal {
    def asAws: aws.MessageType = aws.MessageType.fromValue(message.name)
  }


  implicit class AwsEventSource(val source: aws.EventSource) extends AnyVal {
    def asScala: EventSource = EventSource(source.toString)
  }

  implicit class ScalaEventSource(val source: EventSource) extends AnyVal {
    def asAws: aws.EventSource = aws.EventSource.fromValue(source.name)
  }


  implicit class AwsOwner(val owner: aws.Owner) extends AnyVal {
    def asScala: Owner = Owner(owner.toString)
  }

  implicit class ScalaOwner(val owner: Owner) extends AnyVal {
    def asAws: aws.Owner = aws.Owner.fromValue(owner.name)
  }


  implicit class AwsScope(val scope: aws.Scope) extends AnyVal {
    def asScala: Scope =
      Scope(
        Option(scope.getComplianceResourceId),
        Option(scope.getComplianceResourceTypes).filter(!_.isEmpty).map(_.asScala.toList.map(ResourceType(_))),
        for {
          k <- Option(scope.getTagKey)
          v <- Option(scope.getTagValue)
        } yield (k, v)
      )
  }

  implicit class ScalaScope(val scope: Scope) extends AnyVal {
    def asAws: aws.Scope = {
      val a = new aws.Scope()
      scope.resourceId.foreach(id => a.withComplianceResourceId(id))
      scope.resourceTypes.foreach(ts => a.withComplianceResourceTypes(ts.map(_.name): _*))
      scope.tag.foreach { case (key, value) => a.withTagKey(key); a.withTagValue(value) }
      a
    }
  }

  implicit class AwsResourceType(val resource: aws.ResourceType) extends AnyVal {
    def asScala: ResourceType = ResourceType(resource.toString)
  }

  implicit class ScalaResourceType(val resource: ResourceType) extends AnyVal {
    def asAws: aws.ResourceType = aws.ResourceType.fromValue(resource.toString)
  }

}
