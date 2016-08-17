package com.monsanto.arch.awsutil.lambda

import java.util.concurrent.ExecutorService

import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.services.lambda.AWSLambdaAsyncClient

import com.monsanto.arch.awsutil.impl.ShutdownHook
import com.monsanto.arch.awsutil.lambda.model.FunctionArn
import com.monsanto.arch.awsutil.{Arn, AwsClientProvider, AwsSettings}


/**
  * Created by stevenkohner on 8/16/16.
  */
object Lambda extends AwsClientProvider[StreamingLambdaClient,AsyncLambdaClient] {
  private[awsutil] def init(): Unit = {
    Arn.registerArnPartialFunctions(
      FunctionArn.functionArnPF
    )
  }

  override private[awsutil] def streamingClient(settings: AwsSettings, credentialsProvider: AWSCredentialsProvider,
                                                executorService: ExecutorService): (StreamingLambdaClient, ShutdownHook) = {
    init()
    val aws = new AWSLambdaAsyncClient(credentialsProvider, executorService)
    aws.setRegion(settings.region)
    val client = new DefaultStreamingLambdaClient(aws)
    val shutdownHook = ShutdownHook.clientHook("Lambda", aws)
    (client, shutdownHook)
  }

  override private[awsutil] def asyncClient(streamingClient: StreamingLambdaClient): AsyncLambdaClient =
    new DefaultAsyncLambdaClient(streamingClient)
}
