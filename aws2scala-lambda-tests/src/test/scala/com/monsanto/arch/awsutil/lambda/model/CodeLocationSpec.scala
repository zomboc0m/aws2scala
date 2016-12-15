package com.monsanto.arch.awsutil.lambda.model

import com.amazonaws.services.lambda.{model ⇒ aws}
import com.monsanto.arch.awsutil.converters.LambdaConverters._
import com.monsanto.arch.awsutil.testkit.LambdaScalaCheckImplicits._
import org.scalatest.FreeSpec
import org.scalatest.Matchers._
import org.scalatest.prop.GeneratorDrivenPropertyChecks._

class CodeLocationSpec extends FreeSpec {
  "can be round-tripped" - {
    "from its AWS equivalent" in {
      forAll { scalaCodeLocation: CodeLocation ⇒
        val awsCl =
          new aws.FunctionCodeLocation()
            .withLocation(scalaCodeLocation.location)
            .withRepositoryType(scalaCodeLocation.repositoryType)

        awsCl.asScala.asAws shouldBe awsCl
      }
    }

    "via its AWS equivalent" in {
      forAll { scalaCodeLocation: CodeLocation  ⇒
        scalaCodeLocation.asAws.asScala shouldBe scalaCodeLocation
      }
    }
  }

}
