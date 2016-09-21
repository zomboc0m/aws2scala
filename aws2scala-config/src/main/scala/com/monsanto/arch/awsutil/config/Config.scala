package com.monsanto.arch.awsutil.config

import java.util.concurrent.ExecutorService

import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.services.config.AmazonConfigAsyncClient
import com.monsanto.arch.awsutil.auth.policy.action.ConfigAction
import com.monsanto.arch.awsutil.config.model.ConfigRuleArn
import com.monsanto.arch.awsutil.impl.ShutdownHook
import com.monsanto.arch.awsutil.{Arn, AwsClientProvider, AwsSettings}

object Config extends AwsClientProvider[StreamingConfigClient,AsyncConfigClient] {
  private[awsutil] def init(): Unit = {
    ConfigAction.registerActions()
    Arn.registerArnPartialFunctions(
      ConfigRuleArn.configRuleArnPF
    )
  }

  override private[awsutil] def streamingClient(settings: AwsSettings, credentialsProvider: AWSCredentialsProvider,
                                                executorService: ExecutorService): (StreamingConfigClient, ShutdownHook) = {
    init()
    val aws = new AmazonConfigAsyncClient(credentialsProvider, executorService)
    aws.setRegion(settings.region)
    val client = new DefaultStreamingConfigClient(aws)
    val shutdownHook = ShutdownHook.clientHook("config", aws)
    (client, shutdownHook)
  }

  override private[awsutil] def asyncClient(streamingClient: StreamingConfigClient): AsyncConfigClient =
    new DefaultAsyncConfigClient(streamingClient)
}
