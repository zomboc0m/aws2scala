package com.monsanto.arch.awsutil.config.model

import com.amazonaws.services.config.{model => aws}
import com.monsanto.arch.awsutil.converters.ConfigConverters._
import com.monsanto.arch.awsutil.testkit.ConfigScalaCheckImplicits._
import org.scalatest.FreeSpec
import org.scalatest.Matchers._
import org.scalatest.prop.GeneratorDrivenPropertyChecks._

class PutRuleRequestSpec extends FreeSpec {
  "a PutRuleRequest can be round-tripped" - {
    "from its AWS equivalent" in {
      forAll { rule: ConfigRule ⇒
        val awsRequest = new aws.PutConfigRuleRequest().withConfigRule(rule.asAws)

        awsRequest.asScala.asAws shouldBe awsRequest
      }
    }

    "via its AWS equivalent" in {
      forAll { rule: ConfigRule ⇒
        val request = PutRuleRequest(rule)
        request.asAws.asScala shouldBe request
      }
    }
  }
}
