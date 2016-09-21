package com.monsanto.arch.awsutil.config.model

/**
  * Scope determines what kinds of resources the config rule will evaluate (changes to these resources will
  * trigger evaluation for ConfigurationItemChangeNotification rules)
  *
  * @param resourceId the id of the one resource that will trigger evaluation
  * @param resourceTypes the types of resources that will trigger evaluation.  If resourceId is set, only
  *                      one type can be specified for this parameter
  * @param tag a tag key and value that is applied to only the resources that will trigger an evaluation.
  */
case class Scope(resourceId: Option[String], resourceTypes: Option[Seq[ResourceType]], tag: Option[(String,String)])

