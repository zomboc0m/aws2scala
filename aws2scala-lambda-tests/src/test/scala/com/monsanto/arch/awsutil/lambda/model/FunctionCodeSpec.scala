package com.monsanto.arch.awsutil.lambda.model

import com.amazonaws.services.lambda.{model ⇒ aws}
import com.monsanto.arch.awsutil.converters.LambdaConverters._
import com.monsanto.arch.awsutil.testkit.LambdaScalaCheckImplicits._
import org.scalatest.FreeSpec
import org.scalatest.Matchers._
import org.scalatest.prop.GeneratorDrivenPropertyChecks._

class FunctionCodeSpec extends FreeSpec {
  "can be round-tripped" - {
    "from its AWS equivalent" in {
      forAll { scalaFunctionCode: FunctionCode ⇒
        val awsFC =
          new aws.FunctionCode()
            .withS3Bucket(scalaFunctionCode.S3Bucket.orNull)
            .withS3Key(scalaFunctionCode.S3Key.orNull)
            .withS3ObjectVersion(scalaFunctionCode.S3ObjectVersion.orNull)
            .withZipFile(scalaFunctionCode.ZipFile.orNull)

        awsFC.asScala.asAws shouldBe awsFC
      }
    }

    "via its AWS equivalent" in {
      forAll { scalaFunctionCode: FunctionCode  ⇒
        scalaFunctionCode.asAws.asScala shouldBe scalaFunctionCode
      }
    }
  }
}
