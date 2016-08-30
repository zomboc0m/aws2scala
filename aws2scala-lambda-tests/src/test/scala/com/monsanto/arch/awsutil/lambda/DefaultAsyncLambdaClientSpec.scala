package com.monsanto.arch.awsutil.lambda

import com.monsanto.arch.awsutil.lambda.model.{GetFunctionRequest, LambdaFunction}
import com.monsanto.arch.awsutil.test_support.{FlowMockUtils, Materialised}
import com.monsanto.arch.awsutil.test_support.AdaptableScalaFutures._
import com.monsanto.arch.awsutil.testkit.LambdaScalaCheckImplicits._
import org.scalamock.scalatest.MockFactory
import org.scalatest.Matchers._
import org.scalatest.prop.GeneratorDrivenPropertyChecks._
import org.scalatest.FreeSpec

class DefaultAsyncLambdaClientSpec extends FreeSpec with MockFactory with FlowMockUtils with Materialised {
  "the asynchronous Lambda client should" - {
    "get a lambda function" -{
      "specified by name" in {
        forAll{ (function: LambdaFunction) ⇒
          val streaming = mock[StreamingLambdaClient]("streaming")
          val async = new DefaultAsyncLambdaClient(streaming)

          val request = GetFunctionRequest(function.name)

          (streaming.functionGetter _)
            .expects()
            .returningFlow(request, function)

          val result = async.getFunction(function.name).futureValue
          result shouldBe function
        }
      }

      "specified by ARN" in {
        forAll { (function: LambdaFunction) ⇒
          val streaming = mock[StreamingLambdaClient]("streaming")
          val async = new DefaultAsyncLambdaClient(streaming)

          val request = GetFunctionRequest(function.arn.arnString)

          (streaming.functionGetter _)
            .expects()
            .returningFlow(request, function)

          val result = async.getFunction(function.arn).futureValue
          result shouldBe function
        }
      }
    }
  }
}
