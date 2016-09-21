package com.monsanto.arch.awsutil.lambda.model

import com.monsanto.arch.awsutil.auth.policy.{Action, Principal}

/** AddPermissionRequest represents a request to AWS to add a new permission to an existing lambda function */
case class AddPermissionRequest(
                                 statementId: String,
                                 functionName: String,
                                 principal: Principal,
                                 action: Action,
                                 sourceArn: Option[String] = None,
                                 sourceAccount: Option[String] = None
                               )
