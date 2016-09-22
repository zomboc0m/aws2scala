package com.monsanto.arch.awsutil.config

import akka.Done
import com.monsanto.arch.awsutil.config.model.{ConfigRule, PutRuleRequest}
import com.monsanto.arch.awsutil.test_support.{FlowMockUtils, Materialised}
import com.monsanto.arch.awsutil.test_support.AdaptableScalaFutures._
import com.monsanto.arch.awsutil.testkit.ConfigScalaCheckImplicits._
import com.monsanto.arch.awsutil.testkit.CoreGen
import org.scalamock.scalatest.MockFactory
import org.scalatest.FreeSpec
import org.scalatest.Matchers._
import org.scalatest.prop.GeneratorDrivenPropertyChecks._

class DefaultAsyncConfigClientSpec extends FreeSpec with MockFactory with Materialised with FlowMockUtils {
  "the default async config client should" - {
    "create rules" - {
      "from user specified values" in {
        forAll{ rule: ConfigRule ⇒
          val streaming = mock[StreamingConfigClient]("streaming")
          val async = new DefaultAsyncConfigClient(streaming)

          (streaming.rulePutter _)
            .expects()
            .returningFlow(PutRuleRequest(rule), rule)

          val result = async.putConfigRule(rule).futureValue
          result shouldBe Done
        }
      }
    }

    "delete rules" in {
      forAll(CoreGen.iamName) { ruleName ⇒
        val streaming = mock[StreamingConfigClient]("streaming")
        val async  = new DefaultAsyncConfigClient(streaming)

        (streaming.ruleDeleter _)
          .expects()
          .returningFlow(ruleName,ruleName)

        val result = async.deleteConfigRule(ruleName).futureValue
        result shouldBe Done
      }
    }
  }
}
