package com.monsanto.arch.awsutil.lambda.model

import com.amazonaws.services.lambda.{model ⇒ aws}
import com.monsanto.arch.awsutil.converters.LambdaConverters._
import com.monsanto.arch.awsutil.testkit.LambdaScalaCheckImplicits._
import org.scalatest.FreeSpec
import org.scalatest.Matchers._
import org.scalatest.prop.GeneratorDrivenPropertyChecks._

class GetFunctionRequestSpec extends FreeSpec {
  "can be round-tripped" - {
    "from its AWS equivalent" in {
      forAll { scalaRequest: GetFunctionRequest ⇒
        val awsRequest =
          new aws.GetFunctionRequest()
            .withFunctionName(scalaRequest.name)

        awsRequest.asScala.asAws shouldBe awsRequest
      }
    }

    "via its AWS equivalent" in {
      forAll { scalaRequest: GetFunctionRequest  ⇒
        scalaRequest.asAws.asScala shouldBe scalaRequest
      }
    }
  }
}
