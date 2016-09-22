package com.monsanto.arch.awsutil.config

import akka.stream.Materializer
import akka.stream.scaladsl.{Sink, Source}
import com.monsanto.arch.awsutil.config.model.{ConfigRule, DescribeRulesRequest, PutRuleRequest}

class DefaultAsyncConfigClient(streaming: StreamingConfigClient) extends AsyncConfigClient {
  override def putConfigRule(rule: ConfigRule)(implicit m: Materializer) =
    Source.single(PutRuleRequest(rule))
      .via(streaming.rulePutter)
      .runWith(Sink.ignore)

  override def deleteConfigRule(name: String)(implicit m: Materializer) =
    Source.single(name)
      .via(streaming.ruleDeleter)
      .runWith(Sink.ignore)

  override def describeConfigRules(names: Seq[String])(implicit m: Materializer) =
    Source.single(if (names.isEmpty) DescribeRulesRequest.allRoles else DescribeRulesRequest(Option(names)))
      .via(streaming.ruleDescriber)
      .runWith(Sink.seq)
}
