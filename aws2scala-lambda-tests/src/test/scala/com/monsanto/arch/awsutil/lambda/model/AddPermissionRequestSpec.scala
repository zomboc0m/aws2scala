package com.monsanto.arch.awsutil.lambda.model

import com.amazonaws.services.lambda.{model => aws}
import com.monsanto.arch.awsutil.converters.LambdaConverters._
import com.monsanto.arch.awsutil.testkit.LambdaScalaCheckImplicits._
import org.scalatest.FreeSpec
import org.scalatest.Matchers._
import org.scalatest.prop.GeneratorDrivenPropertyChecks._

class AddPermissionRequestSpec extends FreeSpec {
  "can be round-tripped" - {
    "from its AWS equivalent" in {
      forAll { request: AddPermissionRequest ⇒
        val awsRequest =
          new aws.AddPermissionRequest()
            .withAction(request.action.name)
            .withFunctionName(request.functionName)
            .withPrincipal(request.principal.id)
            .withSourceAccount(request.sourceAccount.orNull)
            .withSourceArn(request.sourceArn.map(_.arnString).orNull)
            .withStatementId(request.statementId)

        awsRequest.asScala.asAws shouldBe awsRequest
      }
    }

    "via its AWS equivalent" in {
      forAll { request: AddPermissionRequest  ⇒
        request.asAws.asScala shouldBe request
      }
    }
  }

}
