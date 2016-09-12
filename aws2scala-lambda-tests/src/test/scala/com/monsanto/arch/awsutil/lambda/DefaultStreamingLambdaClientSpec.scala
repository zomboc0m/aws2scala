package com.monsanto.arch.awsutil.lambda

import java.io.StringWriter

import akka.stream.scaladsl.{Sink, Source}
import com.amazonaws.handlers.AsyncHandler
import com.amazonaws.services.lambda.{AWSLambdaAsync, model => aws}
import com.fasterxml.jackson.core.JsonFactory
import com.monsanto.arch.awsutil.auth.policy.action.LambdaAction
import com.monsanto.arch.awsutil.{Account, Arn}
import com.monsanto.arch.awsutil.auth.policy.{Action, Policy, PolicyJsonSupport, Principal}
import com.monsanto.arch.awsutil.converters.LambdaConverters._
import com.monsanto.arch.awsutil.lambda.model._
import com.monsanto.arch.awsutil.test_support.{AwsMockUtils, Materialised}
import com.monsanto.arch.awsutil.test_support.AdaptableScalaFutures._
import com.monsanto.arch.awsutil.test_support.Samplers._
import com.monsanto.arch.awsutil.testkit.{CoreGen, LambdaGen, UtilGen}
import com.monsanto.arch.awsutil.testkit.CoreScalaCheckImplicits._
import com.monsanto.arch.awsutil.testkit.LambdaScalaCheckImplicits._
import org.scalacheck.Arbitrary._
import org.scalacheck.Gen
import org.scalamock.scalatest.MockFactory
import org.scalatest.Matchers._
import org.scalatest.prop.GeneratorDrivenPropertyChecks._
import org.scalatest.FreeSpec
import spray.json.JsonWriter

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
      forAll { function: LambdaFunction ⇒
        val lambda = mock[AWSLambdaAsync]("lambda")
        val streaming = new DefaultStreamingLambdaClient(lambda)

        val request = GetFunctionRequest(function.name)

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
    //statementId: String, functionName: String, principal: Principal, action: Action, sourceArn: String, sourceAccount: Account
    "a permission adder" in {
      forAll(
        CoreGen.iamName → "id",
        LambdaGen.functionName → "functionName",
        arbitrary[Principal] → "principal",
        Gen.oneOf(LambdaAction.values) → "action",
        Gen.option(arbitrary[Arn]) → "sourceArn",
        Gen.option(arbitrary[Account]) → "sourceAccount"
      ) { (id, functionName, principal, action, sourceArn, sourceAccount) =>
        val lambda = mock[AWSLambdaAsync]("lambda")
        val streaming = new DefaultStreamingLambdaClient(lambda)

        def extractStatement(p: Policy): String = {
          val jsonFactory = new JsonFactory
          val jsonWriter = new StringWriter
          val generator = jsonFactory.createGenerator(jsonWriter)
          generator.writeStartObject()
          generator.writeFieldName("Statement")
          PolicyJsonSupport.statementToJson(generator,p.statements(0))
          generator.writeEndObject()
          generator.flush
          jsonWriter.toString
        }

        val request = AddPermissionRequest(id, functionName, principal, action, sourceArn.map(_.arnString), sourceAccount.map(_.id))
        val createdPolicy = LambdaGen.policyFor(request)

        (lambda.addPermissionAsync(_: aws.AddPermissionRequest, _: AsyncHandler[aws.AddPermissionRequest, aws.AddPermissionResult]))
          .expects(whereRequest { r ⇒
            r should have(
              'action (request.action.name),
              'principal (request.principal.id),
              'statementId (request.statementId),
              'functionName (request.functionName),
              'sourceArn (request.sourceArn.orNull),
              'sourceAccount (request.sourceAccount.orNull)
            )
            true
          })
          .withAwsSuccess(new aws.AddPermissionResult().withStatement(extractStatement(createdPolicy)))

        val result = Source.single(request).via(streaming.permissionAdder).runWith(Sink.head).futureValue
        result shouldBe createdPolicy.statements(0)
      }
    }
  }
}

