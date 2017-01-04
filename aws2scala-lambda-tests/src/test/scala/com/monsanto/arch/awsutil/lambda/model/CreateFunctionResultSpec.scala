package com.monsanto.arch.awsutil.lambda.model

import com.monsanto.arch.awsutil.converters.LambdaConverters._
import com.monsanto.arch.awsutil.testkit.LambdaScalaCheckImplicits._
import org.scalatest.FreeSpec
import org.scalatest.Matchers._
import org.scalatest.prop.GeneratorDrivenPropertyChecks._

class CreateFunctionResultSpec extends FreeSpec {
  "can be round-tripped" - {
    "from its AWS equivalent" in {
      forAll { result: CreateFunctionResult ⇒
        val awsResult = result.asAws

        awsResult.asScala.asAws shouldBe awsResult
      }
    }

    "via its AWS equivalent" in {
      forAll { result: CreateFunctionResult  ⇒
        result.asAws.asScala shouldBe result
      }
    }
  }

}
