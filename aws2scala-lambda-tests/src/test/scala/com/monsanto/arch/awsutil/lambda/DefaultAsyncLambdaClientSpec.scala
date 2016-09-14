package com.monsanto.arch.awsutil.lambda

import java.nio.file.Files

import akka.Done
import com.amazonaws.services.lambda.AWSLambdaAsync
import com.monsanto.arch.awsutil.{Account, Arn}
import com.monsanto.arch.awsutil.auth.policy.{Policy, Principal}
import com.monsanto.arch.awsutil.auth.policy.action.LambdaAction
import com.monsanto.arch.awsutil.lambda.model._
import com.monsanto.arch.awsutil.test_support.{FlowMockUtils, Materialised}
import com.monsanto.arch.awsutil.test_support.Samplers.EnhancedGen
import com.monsanto.arch.awsutil.test_support.AdaptableScalaFutures._
import com.monsanto.arch.awsutil.testkit.{CoreGen, LambdaGen, UtilGen}
import com.monsanto.arch.awsutil.testkit.LambdaScalaCheckImplicits._
import com.monsanto.arch.awsutil.testkit.CoreScalaCheckImplicits._
import org.scalacheck.Arbitrary._
import org.scalacheck.Gen
import org.scalamock.scalatest.MockFactory
import org.scalatest.Matchers._
import org.scalatest.prop.GeneratorDrivenPropertyChecks._
import org.scalatest.FreeSpec

class DefaultAsyncLambdaClientSpec extends FreeSpec with MockFactory with FlowMockUtils with Materialised {
  "the asynchronous Lambda client should" - {
    "create a lambda function" - {
      "using a path to the zipped function code, as well as the function's name, handler, role, and runtime" in {
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

    "delete a lambda function" in {
      forAll(CoreGen.iamName → "functionName") { functionName ⇒
        val streaming = mock[StreamingLambdaClient]("streaming")
        val async = new DefaultAsyncLambdaClient(streaming)

        (streaming.functionDeleter _)
          .expects()
          .returningFlow(functionName, functionName)

        val result = async.deleteFunction(functionName).futureValue
        result shouldBe Done
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
    "add a permission to the lambda function" - {
      "using just the required arguments" in {
        forAll (
          CoreGen.iamName → "id",
          LambdaGen.functionName → "functionName",
          arbitrary[Principal] → "principal",
          Gen.oneOf(LambdaAction.values) → "action"
        ) { (id, functionName, principal, action) =>
          val streaming = mock[StreamingLambdaClient]("streaming")
          val async = new DefaultAsyncLambdaClient((streaming))

          val request = AddPermissionRequest(id, functionName, principal, action)
          val createdPolicyStatement = LambdaGen.policyFor(request).statements(0)

          (streaming.permissionAdder _)
            .expects()
            .returningFlow(request, createdPolicyStatement)

          val result = async.addPermission(id, functionName, principal, action).futureValue
          result shouldBe createdPolicyStatement
        }
      }

      "including a source arn and account" in {
        forAll (
          CoreGen.iamName → "id",
          LambdaGen.functionName → "functionName",
          arbitrary[Principal] → "principal",
          Gen.oneOf(LambdaAction.values) → "action",
          arbitrary[Arn] → "sourceArn",
          arbitrary[Account] → "sourceAccount"
        ) { (id, functionName, principal, action, sourceArn, sourceAccount) =>
          val streaming = mock[StreamingLambdaClient]("streaming")
          val async = new DefaultAsyncLambdaClient((streaming))

          val request = AddPermissionRequest(id, functionName, principal, action, Some(sourceArn.arnString), Some(sourceAccount.id))
          val createdPolicyStatement = LambdaGen.policyFor(request).statements(0)

          (streaming.permissionAdder _)
            .expects()
            .returningFlow(request, createdPolicyStatement)

          val result = async.addPermission(id, functionName, principal, action, sourceArn, sourceAccount).futureValue
          result shouldBe createdPolicyStatement
        }
      }
    }

    "remove a permission from a lambda function" in {
      forAll(CoreGen.iamName → "statementId", CoreGen.iamName → "functionName") { (statementId, functionName) ⇒
        val streaming = mock[StreamingLambdaClient]("streaming")
        val async = new DefaultAsyncLambdaClient(streaming)

        (streaming.permissionRemover _)
          .expects()
          .returningFlow(RemovePermissionRequest(statementId, functionName), statementId)

        val result = async.removePermission(statementId, functionName).futureValue
        result shouldBe Done
      }
    }

    "get the policy associated with a lambda function" - {
      forAll(CoreGen.iamName → "functionName") { functionName ⇒
        val streaming = mock[StreamingLambdaClient]("streaming")
        val async = new DefaultAsyncLambdaClient(streaming)

        val policy = arbitrary[Policy].reallySample(1000)

        (streaming.policyGetter _)
          .expects()
          .returningFlow(functionName, policy)

        val result = async.getPolicy(functionName).futureValue
        result shouldBe policy
      }
    }
  }
}
