package com.monsanto.arch.awsutil.config.model

import com.amazonaws.services.config.{model => aws}
import com.monsanto.arch.awsutil.converters.ConfigConverters._
import com.monsanto.arch.awsutil.testkit.ConfigScalaCheckImplicits._
import org.scalatest.FreeSpec
import org.scalatest.Matchers._
import org.scalatest.prop.GeneratorDrivenPropertyChecks._

class DescribeRulesRequestSpec extends FreeSpec {
  "a DescribeRuleRequest can be round-tripped" - {
    "from its AWS equivalent" in {
      forAll { request: DescribeRulesRequest ⇒
        val awsRequest = new aws.DescribeConfigRulesRequest()
        request.names.foreach(ns => awsRequest.withConfigRuleNames(ns: _*))

        awsRequest.asScala.asAws shouldBe awsRequest
      }
    }

    "via its AWS equivalent" in {
      forAll { request: DescribeRulesRequest ⇒
        request.asAws.asScala shouldBe request
      }
    }
  }
}
