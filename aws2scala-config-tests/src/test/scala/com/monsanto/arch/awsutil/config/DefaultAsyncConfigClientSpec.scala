package com.monsanto.arch.awsutil.config

import akka.Done
import com.monsanto.arch.awsutil.config.model.{ConfigRule, DescribeRulesRequest, PutRuleRequest, StartConfigRuleEvaluationRequest}
import com.monsanto.arch.awsutil.test_support.{FlowMockUtils, Materialised}
import com.monsanto.arch.awsutil.test_support.AdaptableScalaFutures._
import com.monsanto.arch.awsutil.testkit.ConfigScalaCheckImplicits._
import com.monsanto.arch.awsutil.testkit.CoreGen
import org.scalacheck.Gen
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

    "list rules" in {
      forAll(maxSize(20)) { rules: List[ConfigRule] ⇒
        val streaming = mock[StreamingConfigClient]("streaming")
        val async  = new DefaultAsyncConfigClient(streaming)

        val names = rules.map(_.name.getOrElse("testName"))
        val request = if (rules.isEmpty) DescribeRulesRequest.allRoles else DescribeRulesRequest(Some(names))

        (streaming.ruleDescriber _)
          .expects()
          .returningConcatFlow(request,rules)

        val result = async.describeConfigRules(names).futureValue
        result shouldBe rules
      }
    }

    "start evaluations" in {
      forAll(Gen.nonEmptyListOf(CoreGen.iamName), maxSize(25)) { ruleNames: List[String] =>
        val streaming = mock[StreamingConfigClient]("streaming")
        val async  = new DefaultAsyncConfigClient(streaming)

        (streaming.evaluationStarter _).expects().returningFlow(StartConfigRuleEvaluationRequest(ruleNames), ruleNames)

        val result = async.startEvaluations(ruleNames).futureValue
        result shouldBe Done
      }
    }
  }
}
