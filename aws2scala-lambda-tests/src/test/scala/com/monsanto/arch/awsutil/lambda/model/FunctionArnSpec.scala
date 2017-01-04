package com.monsanto.arch.awsutil.lambda.model

import com.monsanto.arch.awsutil.lambda.Lambda
import com.monsanto.arch.awsutil.testkit.LambdaScalaCheckImplicits._
import org.scalatest.FreeSpec
import org.scalatest.Matchers._
import org.scalatest.prop.GeneratorDrivenPropertyChecks._

class FunctionArnSpec extends FreeSpec {
  Lambda.init()

  "a FunctionArn should" - {
    "provide the correct resource" in {
      forAll { arn: FunctionArn ⇒
        arn.resource shouldBe s"function:${arn.name}"
      }
    }

    "produce the correct ARN" in {
      forAll { arn: FunctionArn ⇒
        val partition = arn.account.partition.id
        val accountId = arn.account.id
        val region = arn.region.name
        val functionName = arn.name

        arn.arnString shouldBe s"arn:$partition:lambda:$region:$accountId:function:$functionName"
      }
    }

    "can round-trip via an ARN" in {
      forAll { arn: FunctionArn ⇒
        FunctionArn.fromArnString(arn.arnString) shouldBe arn
      }
    }

    "will fail to parse an invalid ARN" in {
      an [IllegalArgumentException] shouldBe thrownBy {
        FunctionArn.fromArnString("notanarn")
      }
    }
  }
}

