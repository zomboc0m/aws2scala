package com.monsanto.arch.awsutil.lambda

import com.monsanto.arch.awsutil.identitymanagement.model.PolicyArn
import com.monsanto.arch.awsutil.{Arn, AwsClientProvider, AwsSettings}


/**
  * Created by stevenkohner on 8/16/16.
  */
object Lambda extends AwsClientProvider[StreamingLambdaClient,AsyncLambdaClient] {
  private[awsutil] def init(): Unit = {
    Arn.registerArnPartialFunctions(
      PolicyArn.policyArnPF
    )
  }

}
