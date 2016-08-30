package com.monsanto.arch.awsutil.lambda

import akka.stream.scaladsl.{Sink, Source}
import com.amazonaws.handlers.AsyncHandler
import com.amazonaws.services.lambda.{AWSLambdaAsync, model => aws}
import com.monsanto.arch.awsutil.converters.LambdaConverters._
import com.monsanto.arch.awsutil.lambda.model._
import com.monsanto.arch.awsutil.test_support.{AwsMockUtils, Materialised}
import com.monsanto.arch.awsutil.test_support.AdaptableScalaFutures._
import com.monsanto.arch.awsutil.testkit.LambdaScalaCheckImplicits._
import org.scalamock.scalatest.MockFactory
import org.scalatest.Matchers._
import org.scalatest.prop.GeneratorDrivenPropertyChecks._
import org.scalatest.FreeSpec

class DefaultStreamingLambdaClientSpec extends FreeSpec with MockFactory with Materialised with AwsMockUtils {
  "the default StreamingLambdaClient provides" - {
    "a function getter" in {
      forAll { function: LambdaFunction ⇒
        val lambda = mock[AWSLambdaAsync]("lambda")
        val streaming = new DefaultStreamingLambdaClient(lambda)

        val request = GetFunctionRequest(function.name)

        (lambda.getFunctionAsync(_: aws.GetFunctionRequest, _: AsyncHandler[aws.GetFunctionRequest, aws.GetFunctionResult]))
          .expects(whereRequest{ r ⇒
            r.asScala shouldBe request
            true
          })
            .withAwsSuccess(function.asAws)
        val result = Source.single(request).via(streaming.functionGetter).runWith(Sink.head).futureValue
        result shouldBe function
      }
    }
  }
}

