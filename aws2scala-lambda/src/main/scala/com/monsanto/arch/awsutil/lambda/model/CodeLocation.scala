package com.monsanto.arch.awsutil.lambda.model

/** CodeLocation represents information about where the code of the lambda function is stored
  *
  * @param location a presigned URL that can be used to download a .zip file containing the function's code
  * @param repositoryType the kind of repository that the code is stored in; S3, for example
  * */
case class CodeLocation(location: String, repositoryType: String)
