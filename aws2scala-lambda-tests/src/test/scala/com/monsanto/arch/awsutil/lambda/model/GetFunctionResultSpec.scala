package com.monsanto.arch.awsutil.lambda.model

import com.amazonaws.services.lambda.{model ⇒ aws}
import com.monsanto.arch.awsutil.converters.LambdaConverters._
import com.monsanto.arch.awsutil.testkit.LambdaScalaCheckImplicits._
import org.scalatest.FreeSpec
import org.scalatest.Matchers._
import org.scalatest.prop.GeneratorDrivenPropertyChecks._

class GetFunctionResultSpec extends FreeSpec {
  "can be round-tripped" - {
    "from its AWS equivalent" in {
      forAll { scalaResult: GetFunctionResult ⇒
        val awsResult =
          new aws.GetFunctionResult()
            .withCode(scalaResult.codeLocation.asAws)
            .withConfiguration(scalaResult.configuration.asAws)

        awsResult.asScala.asAws shouldBe awsResult
      }
    }

    "via its AWS equivalent" in {
      forAll { scalaResult: GetFunctionResult  ⇒
        scalaResult.asAws.asScala shouldBe scalaResult
      }
    }
  }
}

