package com.monsanto.arch.awsutil.lambda.model

import java.nio.ByteBuffer
import java.nio.file.{Files, Paths}

/**
  * FunctionCode represents the code of a lambda function.  All parameters are optional, but at least one parameter that specifies
  * the location of a .zip file containing the function code must be set.
  *
  * @param S3Bucket        the S3 bucket name where the .zip file containing your deployment package is stored. This bucket must reside in the same AWS region where you are creating the lambda function.
  * @param S3Key           the key name of the S3 object you want to upload
  * @param S3ObjectVersion the version of the S3 object you want to upload
  * @param ZipFile         the binary contents of a zip file containing the deployment package.
  *                        Note: The SDK will automatically encode the .zip file to base64, do not do so yourself
  */
case class FunctionCode(S3Bucket: Option[String] = None,
                        S3Key: Option[String] = None,
                        S3ObjectVersion: Option[String] = None,
                        ZipFile: Option[ByteBuffer] = None)

object FunctionCode {
  /** Builds a FunctionCode object that can be sent to aws
    *
    * @param path the absolute path to a .zip file
    * */
  def fromZipFile(path: String): FunctionCode = {
    val bytes = Files.readAllBytes(Paths.get(path))
    val buffer = ByteBuffer.wrap(bytes)
    FunctionCode(ZipFile = Some(buffer))
  }

  def fromS3Bucket(bucket: String, key: String, version: String = "") =
    FunctionCode(
      Some(bucket),
      Some(key),
      if (version.isEmpty) None else Some(version)
    )
}
