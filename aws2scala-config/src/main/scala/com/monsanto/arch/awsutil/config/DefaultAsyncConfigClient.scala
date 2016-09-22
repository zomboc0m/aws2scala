package com.monsanto.arch.awsutil.config
import akka.Done
import akka.stream.Materializer
import akka.stream.scaladsl.{Sink, Source}
import com.monsanto.arch.awsutil.config.model.{ConfigRule, PutRuleRequest}

import scala.concurrent.Future

class DefaultAsyncConfigClient(streaming: StreamingConfigClient) extends AsyncConfigClient {
  override def putConfigRule(rule: ConfigRule)(implicit m: Materializer): Future[Done] =
    Source.single(PutRuleRequest(rule))
      .via(streaming.rulePutter)
      .runWith(Sink.ignore)

  override def deleteConfigRule(name: String)(implicit m: Materializer): Future[Done] =
    Source.single(name)
      .via(streaming.ruleDeleter)
      .runWith(Sink.ignore)
}
