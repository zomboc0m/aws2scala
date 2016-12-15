package com.monsanto.arch.awsutil.lambda

import java.io.StringWriter

import akka.stream.scaladsl.{Sink, Source}
import com.amazonaws.handlers.AsyncHandler
import com.amazonaws.services.lambda.{AWSLambdaAsync, model => aws}
import com.fasterxml.jackson.core.JsonFactory
import com.monsanto.arch.awsutil.auth.policy.action.LambdaAction
import com.monsanto.arch.awsutil.{Account, Arn}
import com.monsanto.arch.awsutil.auth.policy.{Policy, PolicyJsonSupport, Principal}
import com.monsanto.arch.awsutil.converters.LambdaConverters._
import com.monsanto.arch.awsutil.lambda.model._
import com.monsanto.arch.awsutil.test_support.{AwsMockUtils, Materialised}
import com.monsanto.arch.awsutil.test_support.AdaptableScalaFutures._
import com.monsanto.arch.awsutil.test_support.Samplers._
import com.monsanto.arch.awsutil.testkit.{CoreGen, LambdaGen}
import com.monsanto.arch.awsutil.testkit.CoreScalaCheckImplicits._
import com.monsanto.arch.awsutil.testkit.LambdaScalaCheckImplicits._
import org.scalacheck.Arbitrary._
import org.scalacheck.Gen
import org.scalamock.scalatest.MockFactory
import org.scalatest.Matchers._
import org.scalatest.prop.GeneratorDrivenPropertyChecks._
import org.scalatest.FreeSpec

class DefaultStreamingLambdaClientSpec extends FreeSpec with MockFactory with Materialised with AwsMockUtils {
  "the default StreamingLambdaClient provides" - {
    "a function creator" in {
      forAll { (request: CreateFunctionRequest) ⇒
        val lambda = mock[AWSLambdaAsync]("lambda")
        val streaming = new DefaultStreamingLambdaClient(lambda)
        val createdFunction = LambdaGen.createdFunction(request).reallySample
        (lambda.createFunctionAsync(_: aws.CreateFunctionRequest, _: AsyncHandler[aws.CreateFunctionRequest, aws.CreateFunctionResult]))
          .expects(whereRequest { r ⇒
            r should have(
              'FunctionName (request.name)
            )
            true
          })
          .withAwsSuccess(LambdaGen.createFunctionResultFor(createdFunction))

        val result = Source.single(request).via(streaming.functionCreator).runWith(Sink.head).futureValue
        result shouldBe createdFunction
      }
    }

    "a function deleter" in {
      forAll(LambdaGen.functionName) { functionName ⇒
        val lambda = mock[AWSLambdaAsync]("lambda")
        val streaming = new DefaultStreamingLambdaClient(lambda)

        (lambda.deleteFunctionAsync(_: aws.DeleteFunctionRequest, _: AsyncHandler[aws.DeleteFunctionRequest, aws.DeleteFunctionResult]))
          .expects(whereRequest { r ⇒
            r should have('functionName (functionName))
            true
          })
          .withVoidAwsSuccess()

        val result = Source.single(functionName).via(streaming.functionDeleter).runWith(Sink.head).futureValue
        result shouldBe functionName
      }
    }

    "a function getter" in {
      forAll { function: GetFunctionResult ⇒
        val lambda = mock[AWSLambdaAsync]("lambda")
        val streaming = new DefaultStreamingLambdaClient(lambda)

        val request = GetFunctionRequest(function.configuration.name)

        (lambda.getFunctionAsync(_: aws.GetFunctionRequest, _: AsyncHandler[aws.GetFunctionRequest, aws.GetFunctionResult]))
          .expects(whereRequest { r ⇒
            r.asScala shouldBe request
            true
          })
          .withAwsSuccess(function.asAws)
        val result = Source.single(request).via(streaming.functionGetter).runWith(Sink.head).futureValue
        result shouldBe function
      }
    }

    "a permission adder" in {
      forAll(
        CoreGen.iamName → "id",
        LambdaGen.functionName → "functionName",
        arbitrary[Principal] → "principal",
        Gen.oneOf(LambdaAction.values) → "action",
        Gen.option(arbitrary[Arn]) → "sourceArn",
        Gen.option(CoreGen.accountId) → "sourceAccount"
      ) { (id, functionName, principal, action, sourceArn, sourceAccount) =>
        val lambda = mock[AWSLambdaAsync]("lambda")
        val streaming = new DefaultStreamingLambdaClient(lambda)

        def extractStatement(p: Policy): String = {
          val jsonFactory = new JsonFactory
          val jsonWriter = new StringWriter
          val generator = jsonFactory.createGenerator(jsonWriter)
          generator.writeStartObject()
          generator.writeFieldName("Statement")
          PolicyJsonSupport.statementToJson(generator, p.statements.head)
          generator.writeEndObject()
          generator.flush()
          jsonWriter.toString
        }

        val request = AddPermissionRequest(id, functionName, principal, action, sourceArn, sourceAccount)
        val createdPolicy = LambdaGen.policyFor(request)

        (lambda.addPermissionAsync(_: aws.AddPermissionRequest, _: AsyncHandler[aws.AddPermissionRequest, aws.AddPermissionResult]))
          .expects(whereRequest { r ⇒
            r should have(
              'action (request.action.name),
              'principal (request.principal.id),
              'statementId (request.statementId),
              'functionName (request.functionName),
              'sourceArn (request.sourceArn.map(_.arnString).orNull),
              'sourceAccount (request.sourceAccount.orNull)
            )
            true
          })
          .withAwsSuccess(new aws.AddPermissionResult().withStatement(extractStatement(createdPolicy)))

        val result = Source.single(request).via(streaming.permissionAdder).runWith(Sink.head).futureValue
        result shouldBe createdPolicy.statements.head
      }
    }

    "a permission remover" in {
      forAll(CoreGen.iamName, CoreGen.iamName) { (statementId: String, functionName: String) ⇒
        val lambda = mock[AWSLambdaAsync]("lambda")
        val streaming = new DefaultStreamingLambdaClient(lambda)

        (lambda.removePermissionAsync(_: aws.RemovePermissionRequest, _: AsyncHandler[aws.RemovePermissionRequest, aws.RemovePermissionResult]))
          .expects(whereRequest { r ⇒
            r should have(
              'statementId (statementId),
              'functionName (functionName)
            )
            true
          })
          .withVoidAwsSuccess()

        val result = Source.single(RemovePermissionRequest(statementId, functionName)).via(streaming.permissionRemover).runWith(Sink.head).futureValue
        result shouldBe statementId
      }
    }
  }

//  "a policy getter" in {
//    forAll(CoreGen.iamName) { functionName ⇒
//      val lambda = mock[AWSLambdaAsync]("lambda")
//      val streaming = new DefaultStreamingLambdaClient(lambda)
//
//      val policy = arbitrary[Policy].reallySample(1000)
//
//      (lambda.getPolicyAsync(_: aws.GetPolicyRequest, _: AsyncHandler[aws.GetPolicyRequest,aws.GetPolicyResult]))
//        .expects(whereRequest { r ⇒
//          r should have(
//            'functionName (functionName)
//          )
//          true
//        })
//        .withAwsSuccess(new aws.GetPolicyResult().withPolicy(policy.toJson))
//
//      val result = Source.single(functionName).via(streaming.policyGetter).runWith(Sink.head).futureValue
//      result shouldBe policy
//    }
//  }
}

