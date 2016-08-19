package com.monsanto.arch.awsutil.lambda

import akka.stream.Materializer
import akka.stream.scaladsl.{Sink, Source}
import com.monsanto.arch.awsutil.lambda.model.{FunctionArn, GetFunctionRequest}

/**
  * Created by stevenkohner on 8/16/16.
  */
private[awsutil] class DefaultAsyncLambdaClient(streaming: StreamingLambdaClient) extends AsyncLambdaClient {
  override def getFunction(functionName: String)(implicit m: Materializer) =
    Source.single(GetFunctionRequest(functionName))
      .via(streaming.functionGetter)
      .runWith(Sink.head)

  override def getFunction(functionArn: FunctionArn)(implicit m: Materializer) =
    getFunction(functionArn.toString)(m)
}
