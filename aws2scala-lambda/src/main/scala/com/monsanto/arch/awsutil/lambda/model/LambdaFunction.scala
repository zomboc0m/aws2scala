package com.monsanto.arch.awsutil.lambda.model

import com.monsanto.arch.awsutil.identitymanagement.model.Role

case class LambdaFunction(
                         arn: FunctionArn,
                         name: String,
                         runtime: ???,
                         handler: String,
                         role: Role
                         )
