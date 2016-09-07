package com.monsanto.arch.awsutil.lambda.model

import java.nio.ByteBuffer
import java.nio.file.{Files, Paths}

object DeploymentPackage {
  /**
    * RetrieveBinaryData takes the location of a zip file and fetches the contents for the sdk to upload
    *
    * @param path The absolute path to the .zip file
    */
  private[lambda] def RetrieveBinaryData(path: String): ByteBuffer = {
    val bytes = Files.readAllBytes(Paths.get(path))
    ByteBuffer.wrap(bytes)
  }
}
