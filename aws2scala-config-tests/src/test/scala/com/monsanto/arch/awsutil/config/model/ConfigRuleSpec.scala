package com.monsanto.arch.awsutil.config.model

import com.amazonaws.services.config.{model => aws}
import com.monsanto.arch.awsutil.converters.ConfigConverters._
import com.monsanto.arch.awsutil.testkit.ConfigScalaCheckImplicits._
import org.json4s.DefaultFormats
import org.json4s.native.Json
import org.scalatest.FreeSpec
import org.scalatest.Matchers._
import org.scalatest.prop.GeneratorDrivenPropertyChecks._

class ConfigRuleSpec extends FreeSpec {
  "a ConfigRule can be round-tripped" - {
    "from its AWS equivalent" in {
      forAll { rule: ConfigRule ⇒
        val awsConfigRule = new aws.ConfigRule().withSource(rule.source.asAws)
        rule.arn.foreach(arn => awsConfigRule.withConfigRuleArn(arn.arnString))
        rule.id.foreach(awsConfigRule.withConfigRuleId)
        rule.name.foreach(awsConfigRule.withConfigRuleName)
        rule.ruleState.foreach(st => awsConfigRule.withConfigRuleState(st.asAws))
        rule.description.foreach(awsConfigRule.withDescription)
        rule.inputParameters.foreach(x => awsConfigRule.withInputParameters(Json(DefaultFormats).write(x)))
        rule.maximumExecutionFrequency.foreach(x => awsConfigRule.withMaximumExecutionFrequency(x.asAws))
        rule.scope.foreach(x => awsConfigRule.withScope(x.asAws))

        awsConfigRule.asScala.asAws shouldBe awsConfigRule
      }
    }

    "via its AWS equivalent" in {
      forAll { rule: ConfigRule ⇒
        rule.asAws.asScala shouldBe rule
      }
    }
  }
}
