package com.monsanto.arch.awsutil.lambda.model

import com.monsanto.arch.awsutil.identitymanagement.model.RoleArn

/**
  * CreateFunctionRequest represents a request to aws to create a new lambda function
  *
  * @param code        The code to use with the lambda function
  * @param name        The name of the lambda function
  * @param handler     The function within the code that will be called when the lambda is invoked
  * @param runtime     The language runtime that will run the code
  * @param role        The arn of the role lambda function will assume when it executes the function
  * @param description An optional description of the function
  * @param memory      The amount of memory (in MB) that aws will allocate for the function
  * @param publish     If true, aws will create the function and publish it as an atomic operation
  * @param timeout     The maximum amount of time (in seconds) aws will allow the function to run
  * @param vpcConfig   Subnet and security group id information to allow the function to access resources in a vpc
  */
case class CreateFunctionRequest(code: FunctionCode,
                                 name: String,
                                 handler: String,
                                 runtime: Runtime,
                                 role: RoleArn,
                                 description: Option[String] = None,
                                 memory: Option[Int] = None,
                                 publish: Option[Boolean] = None,
                                 timeout: Option[Int] = None,
                                 vpcConfig: Option[VpcConfig] = None
                                )
