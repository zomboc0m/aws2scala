package com.monsanto.arch.awsutil.lambda.model

import com.monsanto.arch.awsutil.auth.policy.{Action, Principal}

/**
  * Created by stevenkohner on 9/7/16.
  */
case class AddPermissionRequest(
                                 statementId: String,
                                 functionName: String,
                                 principal: Principal,
                                 action: Action,
                                 sourceArn: Option[String] = None,
                                 sourceAccount: Option[String] = None
                                 )
