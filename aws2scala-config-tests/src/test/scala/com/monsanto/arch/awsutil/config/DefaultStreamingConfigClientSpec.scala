package com.monsanto.arch.awsutil.config

import akka.stream.scaladsl.{Sink, Source}
import com.amazonaws.handlers.AsyncHandler
import com.amazonaws.services.config.{AmazonConfigAsync, model => aws}
import com.monsanto.arch.awsutil.config.model.{ConfigRule, PutRuleRequest}
import com.monsanto.arch.awsutil.converters.ConfigConverters._
import com.monsanto.arch.awsutil.testkit.ConfigScalaCheckImplicits._
import com.monsanto.arch.awsutil.test_support.{AwsMockUtils, Materialised}
import com.monsanto.arch.awsutil.test_support.AdaptableScalaFutures._
import org.scalamock.scalatest.MockFactory
import org.scalatest.FreeSpec
import org.scalatest.Matchers._
import org.scalatest.prop.GeneratorDrivenPropertyChecks._

class DefaultStreamingConfigClientSpec extends FreeSpec with MockFactory with Materialised with AwsMockUtils {
  "the default StreamingConfigClient provides" - {
    "a rule putter" in {
      forAll { rule: ConfigRule ⇒
        val config = mock[AmazonConfigAsync]("config")
        val streaming = new DefaultStreamingConfigClient(config)

        (config.putConfigRuleAsync(_: aws.PutConfigRuleRequest, _: AsyncHandler[aws.PutConfigRuleRequest, aws.PutConfigRuleResult]))
          .expects(whereRequest { r ⇒
            r should have(
              'configRule (rule.asAws)
            )
            true
          })
          .withVoidAwsSuccess()

        val result = Source.single(PutRuleRequest(rule)).via(streaming.rulePutter).runWith(Sink.head).futureValue

        result shouldBe rule
      }
    }
  }
}
