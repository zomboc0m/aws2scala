package com.monsanto.arch.awsutil.lambda.model

import java.util.Date

case class LambdaFunction(
                         arn: FunctionArn,
                         name: String,
                         runtime: Runtime,
                         handler: String,
                         role: String,
                         description: Option[String],
                         timeout: Option[Int],
                         lastModified: Option[Date],
                         version: Option[String],
                         codeHash: Option[String],
                         codeLocation: Option[CodeLocation])
