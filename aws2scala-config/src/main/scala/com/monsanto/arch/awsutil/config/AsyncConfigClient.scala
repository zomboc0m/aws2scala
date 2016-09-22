package com.monsanto.arch.awsutil.config

import akka.Done
import akka.stream.Materializer
import com.monsanto.arch.awsutil.AsyncAwsClient
import com.monsanto.arch.awsutil.config.model.ConfigRule

import scala.concurrent.Future


trait AsyncConfigClient extends AsyncAwsClient{
  /** Creates a new config rule based on a rule template supplied by the user. */
  def putConfigRule(rule: ConfigRule)(implicit m: Materializer): Future[Done]

  /** Deletes an existing config rule */
  def deleteConfigRule(name: String)(implicit m: Materializer): Future[Done]
}
