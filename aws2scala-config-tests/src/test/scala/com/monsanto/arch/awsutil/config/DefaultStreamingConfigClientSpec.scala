package com.monsanto.arch.awsutil.config

import akka.stream.scaladsl.{Sink, Source}
import com.amazonaws.handlers.AsyncHandler
import com.amazonaws.services.config.{AmazonConfigAsync, model => aws}
import com.monsanto.arch.awsutil.config.model.{ConfigRule, DescribeRulesRequest, PutRuleRequest}
import com.monsanto.arch.awsutil.converters.ConfigConverters._
import com.monsanto.arch.awsutil.testkit.ConfigScalaCheckImplicits._
import com.monsanto.arch.awsutil.test_support.{AwsMockUtils, Materialised}
import com.monsanto.arch.awsutil.test_support.AdaptableScalaFutures._
import com.monsanto.arch.awsutil.testkit.{ConfigGen, CoreGen}
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

    "a rule deleter" in {
      forAll(CoreGen.iamName) { ruleName ⇒
        val config = mock[AmazonConfigAsync]("config")
        val streaming = new DefaultStreamingConfigClient(config)

        (config.deleteConfigRuleAsync(_: aws.DeleteConfigRuleRequest, _: AsyncHandler[aws.DeleteConfigRuleRequest, aws.DeleteConfigRuleResult]))
          .expects(whereRequest { r ⇒
            r should have('configRuleName (ruleName))
            true
          })
          .withVoidAwsSuccess()

        val result = Source.single(ruleName).via(streaming.ruleDeleter).runWith(Sink.head).futureValue
        result shouldBe ruleName
      }
    }

    "a rule lister" in {
      forAll(maxSize(20)) { rules: List[ConfigRule] ⇒
        val config = mock[AmazonConfigAsync]("config")
        val streaming = new DefaultStreamingConfigClient(config)

        val awsRules = rules.map(_.asAws)
        val names = awsRules.map(_.getConfigRuleName)
        val request = if (rules.isEmpty) DescribeRulesRequest.allRoles else DescribeRulesRequest(Some(names))


        val pages = if (awsRules.isEmpty) List(awsRules) else awsRules.grouped(5).toList

        pages.zipWithIndex.foreach { case (page, i) ⇒
          (config.describeConfigRulesAsync(_: aws.DescribeConfigRulesRequest, _: AsyncHandler[aws.DescribeConfigRulesRequest, aws.DescribeConfigRulesResult]))
            .expects(whereRequest { r ⇒
                val token = if (i == 0) null else i.toString
                r should have(
                  'nextToken (token)
                )
               true})
            .withAwsSuccess {
              val result = new aws.DescribeConfigRulesResult().withConfigRules(page:_*)
              val next = i + 1
              if (next != pages.size) result.setNextToken(next.toString)
              result
            }
        }
        rules.map(_.name)
        val result = Source.single(request).via(streaming.ruleDescriber).runWith(Sink.seq).futureValue

        result shouldBe rules

      }
    }
  }
}
