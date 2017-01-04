package com.monsanto.arch.awsutil.lambda.model

import com.amazonaws.services.lambda.{model ⇒ aws}
import com.monsanto.arch.awsutil.converters.LambdaConverters._
import com.monsanto.arch.awsutil.testkit.LambdaScalaCheckImplicits._
import org.scalatest.FreeSpec
import org.scalatest.Matchers._
import org.scalatest.prop.GeneratorDrivenPropertyChecks._

class FunctionConfigurationSpec extends FreeSpec {
  "can be round-tripped" - {
    "from its AWS equivalent" in {
      forAll { scalaFnC: FunctionConfiguration ⇒
        val awsFnC =
          new aws.FunctionConfiguration()
            .withCodeSha256(scalaFnC.codeHash)
            .withDescription(scalaFnC.description)
            .withFunctionArn(scalaFnC.arn.arnString)
            .withFunctionName(scalaFnC.name)
            .withHandler(scalaFnC.handler)
            .withLastModified(scalaFnC.lastModified)
            .withMemorySize(scalaFnC.memory)
            .withRole(scalaFnC.role)
            .withRuntime(scalaFnC.runtime.name)
            .withTimeout(scalaFnC.timeout)
            .withVersion(scalaFnC.version)
            .withVpcConfig(scalaFnC.vpcConfig.asAws)

        awsFnC.asScala.asAws shouldBe awsFnC
      }
    }

    "via its AWS equivalent" in {
      forAll { scalaFnC: FunctionConfiguration  ⇒
        scalaFnC.asAws.asScala shouldBe scalaFnC
      }
    }
  }
}

