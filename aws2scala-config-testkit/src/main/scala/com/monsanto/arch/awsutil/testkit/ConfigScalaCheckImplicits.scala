package com.monsanto.arch.awsutil.testkit

import com.monsanto.arch.awsutil.Account
import com.monsanto.arch.awsutil.config.model._
import com.monsanto.arch.awsutil.lambda.model.FunctionArn
import com.monsanto.arch.awsutil.regions.Region
import com.monsanto.arch.awsutil.testkit.CoreScalaCheckImplicits._
import com.monsanto.arch.awsutil.testkit.LambdaScalaCheckImplicits.arbFunctionArn
import com.monsanto.arch.awsutil.testkit.UtilGen.{stringOf, wordChar}
import com.monsanto.arch.awsutil.testkit.ConfigGen._
import org.scalacheck.{Arbitrary, Gen, Shrink}
import org.scalacheck.Arbitrary.arbitrary

object ConfigScalaCheckImplicits {

  implicit lazy val arbConfigRule: Arbitrary[ConfigRule] =
    Arbitrary {
      for {
        source <- arbitrary[Source]
        arn <- Gen.option(arbitrary[ConfigRuleArn])
        id <- Gen.option(arbitrary[String])
        name <- Gen.option(stringOf(Arbitrary.arbChar.arbitrary,1,64))
        description <- Gen.option(arbitrary[String])
        inputParameters <- Gen.option(randomJsonParams)
        ruleState <- Gen.option(arbitrary[RuleState])
        maximumExecutionFrequency <- Gen.option(arbitrary[ExecutionFrequency])
        scope <- Gen.option(arbitrary[Scope])
      } yield ConfigRule(source, arn, id, name, description, inputParameters, ruleState, maximumExecutionFrequency, scope)
    }

  implicit lazy val arbOwner: Arbitrary[Owner] =
    Arbitrary {
      Gen.oneOf(Owner.values)
    }

  implicit lazy val arbExecutionFrequency: Arbitrary[ExecutionFrequency] =
    Arbitrary {
      Gen.oneOf(ExecutionFrequency.values)
    }

  implicit lazy val arbManagedRule: Arbitrary[ManagedRule] =
    Arbitrary {
      Gen.oneOf(ManagedRule.values)
    }

  implicit lazy val arbMessageType: Arbitrary[MessageType] =
    Arbitrary {
      Gen.oneOf(MessageType.values)
    }

  implicit lazy val arbResourceType: Arbitrary[ResourceType] =
    Arbitrary {
      Gen.oneOf(ResourceType.values)
    }

  implicit lazy val arbRuleState: Arbitrary[RuleState] =
    Arbitrary {
      Gen.oneOf(RuleState.values)
    }

  implicit lazy val arbEventSource: Arbitrary[EventSource] =
    Arbitrary {
      Gen.oneOf(EventSource.values)
    }

  implicit lazy val arbSourceIdentifier: Arbitrary[SourceIdentifier] =
    Arbitrary {
      Gen.oneOf(arbitrary[ManagedSource], arbitrary[LambdaSource])
    }

  implicit lazy val arbRuleArn: Arbitrary[ConfigRuleArn] =
    Arbitrary {
      for {
        r <- arbitrary[Region]
        a <- arbitrary[Account]
        n <- CoreGen.iamName
      } yield ConfigRuleArn(r, a, n)
    }

  implicit lazy val arbManagedSource: Arbitrary[ManagedSource] =
    Arbitrary {
      for {
        mr <- arbitrary[ManagedRule]
      } yield ManagedSource(mr)
    }

  implicit lazy val arbLambdaSource: Arbitrary[LambdaSource] =
    Arbitrary {
      for {
        arn <- arbitrary[FunctionArn]
      } yield LambdaSource(arn)
    }

  implicit lazy val arbSourceDetail: Arbitrary[SourceDetail] =
    Arbitrary {
      for {
        es <- Gen.option(arbitrary[EventSource])
        ef <- Gen.option(arbitrary[ExecutionFrequency])
        mt <- Gen.option(arbitrary[MessageType])
      } yield SourceDetail(es, ef, mt)
    }

  implicit lazy val arbScope: Arbitrary[Scope] =
    Arbitrary {
      for {
        id <- Gen.option(stringOf(wordChar, 1, 256))
        size <- Gen.choose(1, 100)
        types <- Gen.option(Gen.listOfN(size, arbitrary[ResourceType]))
        key <- arbitrary[String].suchThat(!_.isEmpty)
        value <- arbitrary[String].suchThat(!_.isEmpty)
        kv <- Gen.option((key, value))
      } yield Scope(id, types, kv)
    }

  implicit lazy val arbSource: Arbitrary[Source] =
    Arbitrary {
      for {
        owner <- Gen.option(arbitrary[Owner])
        size <- Gen.choose(1, 25)
        details <- Gen.option(Gen.listOfN(size, arbitrary[SourceDetail]))
        sid <- Gen.option(arbitrary[SourceIdentifier])
      } yield Source(owner, details, sid)
    }

  implicit lazy val arbDescribeRulesRequest: Arbitrary[DescribeRulesRequest] =
    Arbitrary {
      for {
        names <- Gen.option(Gen.nonEmptyListOf(arbitrary[String]))
      } yield DescribeRulesRequest(names)
    }
}
