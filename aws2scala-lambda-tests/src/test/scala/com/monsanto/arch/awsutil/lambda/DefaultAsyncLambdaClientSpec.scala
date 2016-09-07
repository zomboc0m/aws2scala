package com.monsanto.arch.awsutil.lambda

import java.nio.file.Files

import com.monsanto.arch.awsutil.lambda.model.{CreateFunctionRequest, FunctionCode, GetFunctionRequest, LambdaFunction}
import com.monsanto.arch.awsutil.test_support.{FlowMockUtils, Materialised}
import com.monsanto.arch.awsutil.test_support.Samplers.EnhancedGen
import com.monsanto.arch.awsutil.test_support.AdaptableScalaFutures._
import com.monsanto.arch.awsutil.testkit.{CoreGen, LambdaGen, UtilGen}
import com.monsanto.arch.awsutil.testkit.LambdaScalaCheckImplicits._
import org.scalacheck.Arbitrary._
import org.scalamock.scalatest.MockFactory
import org.scalatest.Matchers._
import org.scalatest.prop.GeneratorDrivenPropertyChecks._
import org.scalatest.FreeSpec

class DefaultAsyncLambdaClientSpec extends FreeSpec with MockFactory with FlowMockUtils with Materialised {
  "the asynchronous Lambda client should" - {
    "create a lambda function" - {
      "using a path to the sipped function code, as well as the function's name, handler, role, and runtime" in {
        forAll(
          CoreGen.iamName → "functionName",
          arbitrary[Array[Byte]] → "zipData",
          UtilGen.stringOf(UtilGen.asciiChar, 1, 30) → "handler",
          CoreGen.iamName → "roleName",
          arbitrary[com.monsanto.arch.awsutil.lambda.model.Runtime] → "runtime"
        ) { (functionName, zipData, handler, roleName, runtime) =>
          val streaming = mock[StreamingLambdaClient]
          val async = new DefaultAsyncLambdaClient(streaming)

          val zipPath = Files.write(Files.createTempFile("tmptestlambda", ".zip"), zipData)
          zipPath.toFile.deleteOnExit()

          val request = CreateFunctionRequest(FunctionCode.fromZipFile(zipPath.toAbsolutePath.toString), functionName, handler, runtime, roleName)
          val function = LambdaGen.createdFunction(request).reallySample

          (streaming.functionCreator _).
            expects().
            returningFlow(request, function)

          val result = async.createFunction(zipPath.toAbsolutePath.toString, functionName, handler, roleName, runtime).futureValue
          result shouldBe function
        }
      }

      "by specifying the function's name, code, handler, role, and runtime" in {
        forAll(
          CoreGen.iamName → "functionName",
          arbitrary[FunctionCode] → "functionCode",
          UtilGen.stringOf(UtilGen.asciiChar, 1, 30) → "handler",
          CoreGen.iamName → "roleName",
          arbitrary[com.monsanto.arch.awsutil.lambda.model.Runtime] → "runtime"
        ) { (functionName, functionCode, handler, roleName, runtime) =>

          val streaming = mock[StreamingLambdaClient]("streaming")
          val async = new DefaultAsyncLambdaClient(streaming)

          val request = CreateFunctionRequest(functionCode, functionName, handler, runtime, roleName)
          val function = LambdaGen.createdFunction(request).reallySample

          (streaming.functionCreator _).
            expects().
            returningFlow(request, function)

          val result = async.createFunction(functionCode, functionName, handler, roleName, runtime).futureValue
          result shouldBe function
        }
      }

      "using a customized request" in {
        forAll { r: CreateFunctionRequest =>
          val streaming = mock[StreamingLambdaClient]("streaming")
          val async = new DefaultAsyncLambdaClient(streaming)

          val function = LambdaGen.createdFunction(r).reallySample

          (streaming.functionCreator _).
            expects().
            returningFlow(r, function)

          val result = async.createFunction(r).futureValue
          result shouldBe function
        }
      }
    }
    "get a lambda function" - {
      "specified by name" in {
        forAll { (function: LambdaFunction) ⇒
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
