package com.monsanto.arch.awsutil.config.model

/**
  * Source defines who owns the config rule, as well as what triggers evaluation of a rule and who does the evaluation
  *
  * @param owner specifies who owns the config rule
  * @param sourceDetails defines the source and type of events that will trigger an evaluation of the rule
  * @param sourceIdentifier identifies the lambda function that will evaluate the rule, either an identifier from
  *                         a predefined list or the ARN of a user supplied function
  *
  */
case class Source(owner: Option[Owner], sourceDetails: Option[Seq[SourceDetail]], sourceIdentifier: Option[SourceIdentifier])
