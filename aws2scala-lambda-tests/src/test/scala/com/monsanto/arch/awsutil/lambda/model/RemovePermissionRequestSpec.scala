package com.monsanto.arch.awsutil.lambda.model

import com.amazonaws.services.lambda.{model ⇒ aws}
import com.monsanto.arch.awsutil.converters.LambdaConverters._
import com.monsanto.arch.awsutil.testkit.LambdaScalaCheckImplicits._
import org.scalatest.FreeSpec
import org.scalatest.Matchers._
import org.scalatest.prop.GeneratorDrivenPropertyChecks._

class RemovePermissionRequestSpec extends FreeSpec {
  "can be round-tripped" - {
    "from its AWS equivalent" in {
      forAll { scalaRequest: RemovePermissionRequest ⇒
        val awsRequest =
          new aws.RemovePermissionRequest().withFunctionName(scalaRequest.functionName).withStatementId(scalaRequest.statementId)

        awsRequest.asScala.asAws shouldBe awsRequest
      }
    }

    "via its AWS equivalent" in {
      forAll { scalaRequest: RemovePermissionRequest  ⇒
        scalaRequest.asAws.asScala shouldBe scalaRequest
      }
    }
  }
}
