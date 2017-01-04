package com.monsanto.arch.awsutil.lambda.model

import com.monsanto.arch.awsutil.converters.LambdaConverters._
import com.monsanto.arch.awsutil.testkit.LambdaScalaCheckImplicits._
import org.scalatest.FreeSpec
import org.scalatest.Matchers._
import org.scalatest.prop.GeneratorDrivenPropertyChecks._

class CreateFunctionRequestSpec extends FreeSpec {
  "can be round-tripped" - {
    "from its AWS equivalent" in {
      forAll { request: CreateFunctionRequest ⇒
        val awsRequest = request.asAws

        awsRequest.asScala.asAws shouldBe awsRequest
      }
    }

    "via its AWS equivalent" in {
      forAll { request: CreateFunctionRequest  ⇒
        request.asAws.asScala shouldBe request
      }
    }
  }
}
