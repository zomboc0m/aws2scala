package com.monsanto.arch.awsutil.lambda.model

import org.scalatest.FreeSpec
import org.scalatest.Matchers._
import org.scalatest.prop.GeneratorDrivenPropertyChecks._
import com.monsanto.arch.awsutil.converters.LambdaConverters._
import com.monsanto.arch.awsutil.testkit.LambdaScalaCheckImplicits._

class GetFunctionRequestSpec extends FreeSpec {
  "a GetFunctionRequest instance will" - {
    "correctly convert to its AWS equivalent" in {
      forAll { request: GetFunctionRequest ⇒
        request.asAws should have (
          'functionName (request.name)
        )
      }
    }
  }
}
