package com.monsanto.arch.awsutil.auth.policy

import java.nio.ByteBuffer
import java.util.{Base64, Date}

import akka.util.ByteString
import com.amazonaws.auth.policy.conditions._
import com.monsanto.arch.awsutil.converters.CoreConverters._
import com.monsanto.arch.awsutil.test_support.AwsEnumerationBehaviours
import com.monsanto.arch.awsutil.testkit.CoreScalaCheckImplicits._
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.FreeSpec
import org.scalatest.Inside._
import org.scalatest.Matchers._
import org.scalatest.prop.GeneratorDrivenPropertyChecks._
import org.scalatest.prop.TableDrivenPropertyChecks.{Table, forAll ⇒ forAllIn}

import scala.collection.JavaConverters._

class ConditionSpec extends FreeSpec with AwsEnumerationBehaviours {
  "Condition should" - {
    "generate the correct AWS equivalent" in {
      forAll { condition: Condition ⇒
        condition.asAws should have (
          'conditionKey (condition.key),
          'type (condition.comparisonType),
          'values (condition.comparisonValues.asJava)
        )
      }
    }

    "round-trip through its AWS equivalent" in {
      forAll { condition: Condition ⇒
        condition.asAws.asScala shouldBe condition
      }
    }

    "extract from its parts" in {
      forAll { condition: Condition ⇒
        inside((condition.key, condition.comparisonType, condition.comparisonValues)) {
          case Condition.fromParts(c) ⇒ c shouldBe condition
        }
      }
    }

    "be built from its parts" in {
      forAll { condition: Condition ⇒
        Condition.fromParts(condition.key, condition.comparisonType, condition.comparisonValues) shouldBe condition
      }
    }

    "fail to build from invalid parts" in {
      forAll { (key: String, rawComparisonType: String, values: Seq[String], maybeSetOp: Option[Condition.SetOperation]) ⇒
        val comparisonType = maybeSetOp.map(op ⇒ s"${op.prefix}$rawComparisonType").getOrElse(rawComparisonType)
        whenever(Condition.fromParts.unapply((key, comparisonType, values)).isEmpty) {
          an [IllegalArgumentException] shouldBe thrownBy {
            Condition.fromParts(key, comparisonType, values)
          }
        }
      }
    }

    "provide convenience keys for" - {
      "source ARNs" in {
        val result = Condition.sourceArn is "foo"
        result.key shouldBe ConditionFactory.SOURCE_ARN_CONDITION_KEY
      }

      "current time" in {
        val result = Condition.currentTime is new Date()
        result.key shouldBe ConditionFactory.CURRENT_TIME_CONDITION_KEY
      }

      "epoch time" in {
        val result = Condition.epochTime is new Date()
        result.key shouldBe ConditionFactory.EPOCH_TIME_CONDITION_KEY
      }

      "source IP" in {
        Condition.sourceIp shouldBe Condition.IpAddressKey(ConditionFactory.SOURCE_IP_CONDITION_KEY, ignoreMissing = false)
      }
    }

    "provide methods for generating" - {
      "ARN conditions" - {
        "specifying ifExists on the key" in {
          forAll { condition: Condition.ArnCondition ⇒
            val key =
              if (condition.ignoreMissing) {
                Condition.arn(condition.key).ifExists
              } else {
                Condition.arn(condition.key)
              }
            val result =
              condition.arnComparisonType match {
                case Condition.ArnComparisonType.Equals ⇒ key.is(condition.comparisonValues: _*)
                case Condition.ArnComparisonType.NotEquals ⇒ key.isNot(condition.comparisonValues: _*)
                case Condition.ArnComparisonType.Like ⇒ key.isLike(condition.comparisonValues: _*)
                case Condition.ArnComparisonType.NotLike ⇒ key.isNotLike(condition.comparisonValues: _*)
              }
            result shouldBe condition
          }
        }

        "specifying ifExists on the condition" in {
          forAll { condition: Condition.ArnCondition ⇒
            val key = Condition.arn(condition.key)
            val baseCondition =
              condition.arnComparisonType match {
                case Condition.ArnComparisonType.Equals ⇒ key.is(condition.comparisonValues: _*)
                case Condition.ArnComparisonType.NotEquals ⇒ key.isNot(condition.comparisonValues: _*)
                case Condition.ArnComparisonType.Like ⇒ key.isLike(condition.comparisonValues: _*)
                case Condition.ArnComparisonType.NotLike ⇒ key.isNotLike(condition.comparisonValues: _*)
              }
            val result =
              if (condition.ignoreMissing) {
                baseCondition.ifExists
              } else {
                baseCondition
              }
            result shouldBe condition
          }
        }
      }

      "binary conditions" - {
        "using an array of bytes" in {
          forAll(Gen.identifier, arbitrary[Array[Byte]]) { (key, bytes) ⇒
            val result = Condition.binary(key) is bytes
            result shouldBe Condition.BinaryCondition(key, Seq(ByteString(bytes)), ignoreMissing = false)
          }
        }

        "using a ByteBuffer" in {
          forAll(Gen.identifier, arbitrary[Array[Byte]].map(ByteBuffer.wrap)) { (key, bytes) ⇒
            val result = Condition.binary(key) is bytes
            result shouldBe Condition.BinaryCondition(key, Seq(ByteString(bytes)), ignoreMissing = false)
          }
        }

        "using a ByteString" in {
          forAll(Gen.identifier, arbitrary[Array[Byte]].map(ByteString(_))) { (key, bytes) ⇒
            val result = Condition.binary(key) is bytes
            result shouldBe Condition.BinaryCondition(key, Seq(bytes), ignoreMissing = false)
          }
        }

        "specifying ifExists on the BinaryKey" in {
          forAll(Gen.identifier, arbitrary[Array[Byte]].map(ByteString(_))) { (key, bytes) ⇒
            val result = Condition.binary(key).ifExists is bytes
            result shouldBe Condition.BinaryCondition(key, Seq(bytes), ignoreMissing = true)
          }
        }

        "specifying ifExists on the BinaryCondition" in {
          forAll(Gen.identifier, arbitrary[Array[Byte]].map(ByteString(_))) { (key, bytes) ⇒
            val result = (Condition.binary(key) is bytes).ifExists
            result shouldBe Condition.BinaryCondition(key, Seq(bytes), ignoreMissing = true)
          }
        }
      }

      "boolean conditions" - {
        "specifying ifExists on the key" in {
          forAll { condition: Condition.BooleanCondition ⇒
            val key =
              if (condition.ignoreMissing) {
                Condition.boolean(condition.key).ifExists
              } else {
                Condition.boolean(condition.key)
              }
            val result = if (condition.value) key.isTrue else key.isFalse
            result shouldBe condition
          }
        }

        "specifying ifExists on the condition" in {
          forAll { condition: Condition.BooleanCondition ⇒
            val key = Condition.boolean(condition.key)
            val baseCondition = if (condition.value) key.isTrue else key.isFalse
            val result =
              if (condition.ignoreMissing) {
                baseCondition.ifExists
              } else {
                baseCondition
              }
            result shouldBe condition
          }
        }
      }

      "date conditions" - {
        "specifying ifExists on the key" in {
          forAll { condition: Condition.DateCondition ⇒
            val key =
              if (condition.ignoreMissing) {
                Condition.date(condition.key).ifExists
              } else {
                Condition.date(condition.key)
              }
            val result =
              condition.dateComparisonType match {
                case Condition.DateComparisonType.Equals ⇒ key.is(condition.values: _*)
                case Condition.DateComparisonType.NotEquals ⇒ key.isNot(condition.values: _*)
                case Condition.DateComparisonType.After ⇒ key.isAfter(condition.values: _*)
                case Condition.DateComparisonType.AtOrAfter ⇒ key.isAtOrAfter(condition.values: _*)
                case Condition.DateComparisonType.Before ⇒ key.isBefore(condition.values: _*)
                case Condition.DateComparisonType.AtOrBefore ⇒ key.isAtOrBefore(condition.values: _*)
              }
            result shouldBe condition
          }
        }

        "specifying ifExists on the condition" in {
          forAll { condition: Condition.DateCondition ⇒
            val key = Condition.date(condition.key)
            val baseCondition =
              condition.dateComparisonType match {
                case Condition.DateComparisonType.Equals ⇒ key.is(condition.values: _*)
                case Condition.DateComparisonType.NotEquals ⇒ key.isNot(condition.values: _*)
                case Condition.DateComparisonType.After ⇒ key.isAfter(condition.values: _*)
                case Condition.DateComparisonType.AtOrAfter ⇒ key.isAtOrAfter(condition.values: _*)
                case Condition.DateComparisonType.Before ⇒ key.isBefore(condition.values: _*)
                case Condition.DateComparisonType.AtOrBefore ⇒ key.isAtOrBefore(condition.values: _*)
              }
            val result =
              if (condition.ignoreMissing) {
                baseCondition.ifExists
              } else {
                baseCondition
              }
            result shouldBe condition
          }
        }
      }

      "IP address conditions" - {
        "specifying ifExists on the key" in {
          forAll { condition: Condition.IpAddressCondition ⇒
            val key =
              if (condition.ignoreMissing) {
                Condition.ipAddress(condition.key).ifExists
              } else {
                Condition.ipAddress(condition.key)
              }
            val result =
              condition.ipAddressComparisonType match {
                case Condition.IpAddressComparisonType.IsIn ⇒ key.isIn(condition.cidrBlocks: _*)
                case Condition.IpAddressComparisonType.IsNotIn ⇒ key.isNotIn(condition.cidrBlocks: _*)
              }
            result shouldBe condition
          }
        }

        "specifying ifExists on the condition" in {
          forAll { condition: Condition.IpAddressCondition ⇒
            val key = Condition.ipAddress(condition.key)
            val baseCondition =
              condition.ipAddressComparisonType match {
                case Condition.IpAddressComparisonType.IsIn ⇒ key.isIn(condition.cidrBlocks: _*)
                case Condition.IpAddressComparisonType.IsNotIn ⇒ key.isNotIn(condition.cidrBlocks: _*)
              }
            val result =
              if (condition.ignoreMissing) {
                baseCondition.ifExists
              } else {
                baseCondition
              }
            result shouldBe condition
          }
        }
      }

      "null conditions" in {
        forAll { condition: Condition.NullCondition ⇒
          val result =
            if (condition.value) {
              Condition.isMissing(condition.key)
            } else {
              Condition.isNotNull(condition.key)
            }
          result shouldBe condition
        }
      }

      "numeric conditions" - {
        "specifying ifExists on the key" in {
          forAll { condition: Condition.NumericCondition ⇒
            val key =
              if (condition.ignoreMissing) {
                Condition.numeric(condition.key).ifExists
              } else {
                Condition.numeric(condition.key)
              }
            val result =
              condition.numericComparisonType match {
                case Condition.NumericComparisonType.Equals ⇒ key.is(condition.values: _*)
                case Condition.NumericComparisonType.GreaterThan ⇒ key.isGreaterThan(condition.values: _*)
                case Condition.NumericComparisonType.GreaterThanEquals ⇒ key.isGreaterThanOrEqualTo(condition.values: _*)
                case Condition.NumericComparisonType.LessThan ⇒ key.isLessThan(condition.values: _*)
                case Condition.NumericComparisonType.LessThanEquals ⇒ key.isLessThanOrEqualTo(condition.values: _*)
                case Condition.NumericComparisonType.NotEquals ⇒ key.isNot(condition.values: _*)
              }
            result shouldBe condition
          }
        }

        "specifying ifExists on the condition" in {
          forAll { condition: Condition.NumericCondition ⇒
            val key = Condition.numeric(condition.key)
            val baseCondition =
              condition.numericComparisonType match {
                case Condition.NumericComparisonType.Equals ⇒ key.is(condition.values: _*)
                case Condition.NumericComparisonType.GreaterThan ⇒ key.isGreaterThan(condition.values: _*)
                case Condition.NumericComparisonType.GreaterThanEquals ⇒ key.isGreaterThanOrEqualTo(condition.values: _*)
                case Condition.NumericComparisonType.LessThan ⇒ key.isLessThan(condition.values: _*)
                case Condition.NumericComparisonType.LessThanEquals ⇒ key.isLessThanOrEqualTo(condition.values: _*)
                case Condition.NumericComparisonType.NotEquals ⇒ key.isNot(condition.values: _*)
              }
            val result =
              if (condition.ignoreMissing) {
                baseCondition.ifExists
              } else {
                baseCondition
              }
            result shouldBe condition
          }
        }
      }

      "string conditions" - {
        "specifying ifExists on the key" in {
          forAll { condition: Condition.StringCondition ⇒
            val key =
              if (condition.ignoreMissing) {
                Condition.string(condition.key).ifExists
              } else {
                Condition.string(condition.key)
              }
            val result =
              condition.stringComparisonType match {
                case Condition.StringComparisonType.Equals ⇒ key.is(condition.comparisonValues: _*)
                case Condition.StringComparisonType.NotEquals ⇒ key.isNot(condition.comparisonValues: _*)
                case Condition.StringComparisonType.EqualsIgnoreCase ⇒ key.ignoringCaseIs(condition.comparisonValues: _*)
                case Condition.StringComparisonType.NotEqualsIgnoreCase ⇒ key.ignoringCaseIsNot(condition.comparisonValues: _*)
                case Condition.StringComparisonType.Like ⇒ key.isLike(condition.comparisonValues: _*)
                case Condition.StringComparisonType.NotLike ⇒ key.isNotLike(condition.comparisonValues: _*)
              }
            result shouldBe condition
          }
        }

        "specifying ifExists on the condition" in {
          forAll { condition: Condition.StringCondition ⇒
            val key = Condition.string(condition.key)
            val baseCondition =
              condition.stringComparisonType match {
                case Condition.StringComparisonType.Equals ⇒ key.is(condition.comparisonValues: _*)
                case Condition.StringComparisonType.NotEquals ⇒ key.isNot(condition.comparisonValues: _*)
                case Condition.StringComparisonType.EqualsIgnoreCase ⇒ key.ignoringCaseIs(condition.comparisonValues: _*)
                case Condition.StringComparisonType.NotEqualsIgnoreCase ⇒ key.ignoringCaseIsNot(condition.comparisonValues: _*)
                case Condition.StringComparisonType.Like ⇒ key.isLike(condition.comparisonValues: _*)
                case Condition.StringComparisonType.NotLike ⇒ key.isNotLike(condition.comparisonValues: _*)
              }
            val result =
              if (condition.ignoreMissing) {
                baseCondition.ifExists
              } else {
                baseCondition
              }
            result shouldBe condition
          }
        }
      }
    }
  }

  "Condition.ArnCondition should" - {
    "have the correct comparison type" in {
      forAll { condition: Condition.ArnCondition ⇒
        condition.comparisonType shouldBe
          (condition.arnComparisonType.id + (if (condition.ignoreMissing) "IfExists" else ""))
      }
    }

    "have the correct comparison values" in {
      forAll { condition: Condition.ArnCondition ⇒
        condition.comparisonValues shouldBe condition.values
      }
    }

    "be extractable from its parts" in {
      forAll { condition: Condition.ArnCondition ⇒
        inside((condition.key, condition.comparisonType, condition.comparisonValues)) {
          case Condition.ArnCondition.fromParts(c) ⇒ c shouldBe condition
        }
      }
    }

    behave like multiValueSupportCondition[Condition.ArnCondition]
  }

  "Condition.ArnComparisonType enumeration" - {
    val comparisonTypes = Table("ARN comparison types", Condition.ArnComparisonType.values: _*)

    behave like anAwsEnumeration(
      ArnCondition.ArnComparisonType.values,
      Condition.ArnComparisonType.values,
      (_: Condition.ArnComparisonType).asAws,
      (_: ArnCondition.ArnComparisonType).asScala)

    "should have an id that matches the AWS name" in {
      forAllIn(comparisonTypes) { comparisonType ⇒
        comparisonType.id shouldBe comparisonType.asAws.name()
      }
    }

    "should be recoverable from an ID" in {
      forAllIn(comparisonTypes) { comparisonType ⇒
        Condition.ArnComparisonType.fromId(comparisonType.id) shouldBe theSameInstanceAs (comparisonType)
      }
    }

    "should not build from invalid IDs" in {
      val isValidId = Condition.ArnComparisonType.values.map(_.id).toSet
      forAll { badId: String ⇒
        whenever(!isValidId(badId)) {
          an [IllegalArgumentException] shouldBe thrownBy {
            Condition.ArnComparisonType.fromId(badId)
          }
        }
      }
    }
  }

  "Condition.BinaryCondition should" - {
    "have the correct comparison type" in {
      forAll { condition: Condition.BinaryCondition ⇒
        condition.comparisonType shouldBe (if (condition.ignoreMissing)  "BinaryIfExists" else "Binary")
      }
    }

    "have the correct comparison values" in {
      forAll { condition: Condition.BinaryCondition ⇒
        condition.comparisonValues shouldBe condition.values.map(v ⇒ Base64.getEncoder.encodeToString(v.toArray))
      }
    }

    "be extractable from its parts" in {
      forAll { condition: Condition.BinaryCondition ⇒
        inside((condition.key, condition.comparisonType, condition.comparisonValues)) {
          case Condition.BinaryCondition.fromParts(c) ⇒ c shouldBe condition
        }
      }
    }

    behave like multiValueSupportCondition[Condition.BinaryCondition]
  }

  "Condition.BooleanCondition should" - {
    "have the correct comparison type" in {
      forAll { condition: Condition.BooleanCondition ⇒
        condition.comparisonType shouldBe (if (condition.ignoreMissing)  "BoolIfExists" else "Bool")
      }
    }

    "have the correct comparison values" in {
      forAll { condition: Condition.BooleanCondition ⇒
        condition.comparisonValues shouldBe Seq(condition.value.toString)
      }
    }

    "be extractable from its parts" in {
      forAll { condition: Condition.BooleanCondition ⇒
        inside((condition.key, condition.comparisonType, condition.comparisonValues)) {
          case Condition.BooleanCondition.fromParts(c) ⇒ c shouldBe condition
        }
      }
    }

    "not support values that are not a single boolean" in {
      forAll(
        arbitrary[Condition.BooleanCondition] → "baseCondition",
        nonSingularBooleanValues → "badValues"
      ) { (baseCondition, badValues) ⇒
        val parts: (String, String, Seq[String]) = (baseCondition.key, baseCondition.comparisonType, badValues)
        Condition.BooleanCondition.fromParts.unapply(parts) shouldBe None
      }
    }

    behave like multiValueSupportCondition[Condition.BooleanCondition]
  }

  "Condition.DateCondition should" - {
    "have the correct comparison type" in {
      forAll { condition: Condition.DateCondition ⇒
        condition.comparisonType shouldBe
          (condition.dateComparisonType.id + (if (condition.ignoreMissing)  "IfExists" else ""))
      }
    }

    "have the correct comparison values" in {
      forAll { condition: Condition.DateCondition ⇒
        condition.comparisonValues shouldBe condition.values.map(_.toInstant.toString)
      }
    }

    "be extractable from its parts" in {
      forAll { condition: Condition.DateCondition ⇒
        inside((condition.key, condition.comparisonType, condition.comparisonValues)) {
          case Condition.DateCondition.fromParts(c) ⇒ c shouldBe condition
        }
      }
    }

    behave like multiValueSupportCondition[Condition.DateCondition]
  }

  "Condition.DateComparisonType enumeration" - {
    val comparisonTypes = Table("date comparison type", Condition.DateComparisonType.values: _*)

    behave like anAwsEnumeration(
      DateCondition.DateComparisonType.values, Condition.DateComparisonType.values,
      (_: Condition.DateComparisonType).asAws, (_: DateCondition.DateComparisonType).asScala)

    "should have an id that matches the AWS name" in {
      forAllIn(comparisonTypes) { comparisonType ⇒
        comparisonType.id shouldBe comparisonType.asAws.name()
      }
    }

    "should be recoverable from an ID" in {
      forAllIn(comparisonTypes) { comparisonType ⇒
        Condition.DateComparisonType.fromId(comparisonType.id) shouldBe theSameInstanceAs (comparisonType)
      }
    }

    "should not build from invalid IDs" in {
      val isValidId = Condition.DateComparisonType.values.map(_.id).toSet
      forAll { badId: String ⇒
        whenever(!isValidId(badId)) {
          an [IllegalArgumentException] shouldBe thrownBy {
            Condition.DateComparisonType.fromId(badId)
          }
        }
      }
    }
  }

  "Condition.IpAddressCondition should" - {
    "have the correct comparison type" in {
      forAll { condition: Condition.IpAddressCondition ⇒
        condition.comparisonType shouldBe
          (condition.ipAddressComparisonType.id + (if (condition.ignoreMissing)  "IfExists" else ""))
      }
    }

    "have the correct comparison values" in {
      forAll { condition: Condition.IpAddressCondition ⇒
        condition.comparisonValues shouldBe condition.cidrBlocks
      }
    }

    "be extractable from its parts" in {
      forAll { condition: Condition.IpAddressCondition ⇒
        inside((condition.key, condition.comparisonType, condition.comparisonValues)) {
          case Condition.IpAddressCondition.fromParts(c) ⇒ c shouldBe condition
        }
      }
    }

    behave like multiValueSupportCondition[Condition.IpAddressCondition]
  }

  "Condition.IpAddressComparisonType enumeration" - {
    val comparisonTypes = Table("IP address comparison type", Condition.IpAddressComparisonType.values: _*)

    behave like anAwsEnumeration(
      IpAddressCondition.IpAddressComparisonType.values, Condition.IpAddressComparisonType.values,
      (_: Condition.IpAddressComparisonType).asAws, (_: IpAddressCondition.IpAddressComparisonType).asScala)

    "should have an id that matches the AWS name" in {
      forAllIn(comparisonTypes) { comparisonType ⇒
        comparisonType.id shouldBe comparisonType.asAws.name()
      }
    }

    "should be recoverable from an ID" in {
      forAllIn(comparisonTypes) { comparisonType ⇒
        Condition.IpAddressComparisonType.fromId(comparisonType.id) shouldBe theSameInstanceAs (comparisonType)
      }
    }

    "should not build from invalid IDs" in {
      val isValidId = Condition.IpAddressComparisonType.values.map(_.id).toSet
      forAll { badId: String ⇒
        whenever(!isValidId(badId)) {
          an [IllegalArgumentException] shouldBe thrownBy {
            Condition.IpAddressComparisonType.fromId(badId)
          }
        }
      }
    }
  }

  "Condition.NumericCondition should" - {
    "have the correct comparison type" in {
      forAll { condition: Condition.NumericCondition ⇒
        condition.comparisonType shouldBe
          (condition.numericComparisonType.id + (if (condition.ignoreMissing) "IfExists" else ""))
      }
    }

    "have the correct comparison values" in {
      forAll { condition: Condition.NumericCondition ⇒
        condition.comparisonValues shouldBe condition.values.map(_.toString)
      }
    }

    "be extractable from its parts" in {
      forAll { condition: Condition.NumericCondition ⇒
        inside((condition.key, condition.comparisonType, condition.comparisonValues)) {
          case Condition.NumericCondition.fromParts(c) ⇒ c shouldBe condition
        }
      }
    }

    behave like multiValueSupportCondition[Condition.NumericCondition]
  }

  "Condition.NumericComparisonType enumeration" - {
    val comparisonTypes = Table("numeric comparison type", Condition.NumericComparisonType.values: _*)

    behave like anAwsEnumeration(
      NumericCondition.NumericComparisonType.values, Condition.NumericComparisonType.values,
      (_: Condition.NumericComparisonType).asAws, (_: NumericCondition.NumericComparisonType).asScala)

    "should have an id that matches the AWS name" in {
      forAllIn(comparisonTypes) { comparisonType ⇒
        comparisonType.id shouldBe comparisonType.asAws.name()
      }
    }

    "should be recoverable from an ID" in {
      forAllIn(comparisonTypes) { comparisonType ⇒
        Condition.NumericComparisonType.fromId(comparisonType.id) shouldBe theSameInstanceAs (comparisonType)
      }
    }

    "should not build from invalid IDs" in {
      val isValidId = Condition.NumericComparisonType.values.map(_.id).toSet
      forAll { badId: String ⇒
        whenever(!isValidId(badId)) {
          an [IllegalArgumentException] shouldBe thrownBy {
            Condition.NumericComparisonType.fromId(badId)
          }
        }
      }
    }
  }

  "Condition.StringCondition should" - {
    "have the correct comparison type" in {
      forAll { condition: Condition.StringCondition ⇒
        condition.comparisonType shouldBe
          (condition.stringComparisonType.id + (if (condition.ignoreMissing)  "IfExists" else ""))
      }
    }

    "be extractable from its parts" in {
      forAll { condition: Condition.StringCondition ⇒
        inside((condition.key, condition.comparisonType, condition.comparisonValues)) {
          case Condition.StringCondition.fromParts(c) ⇒ c shouldBe condition
        }
      }
    }

    behave like multiValueSupportCondition[Condition.StringCondition]
  }

  "Condition.StringComparisonType enumeration" - {
    val comparisonTypes = Table("string comparison type", Condition.StringComparisonType.values: _*)

    "should have an id that matches the AWS name" in {
      forAllIn(comparisonTypes) { comparisonType ⇒
        comparisonType.id shouldBe comparisonType.asAws.name()
      }
    }

    "should be recoverable from an ID" in {
      forAllIn(comparisonTypes) { comparisonType ⇒
        Condition.StringComparisonType.fromId(comparisonType.id) shouldBe theSameInstanceAs (comparisonType)
      }
    }

    "should not build from invalid IDs" in {
      val isValidId = Condition.StringComparisonType.values.map(_.id).toSet
      forAll { badId: String ⇒
        whenever(!isValidId(badId)) {
          an [IllegalArgumentException] shouldBe thrownBy {
            Condition.StringComparisonType.fromId(badId)
          }
        }
      }
    }

    behave like anAwsEnumeration(
      StringCondition.StringComparisonType.values, Condition.StringComparisonType.values,
      (_: Condition.StringComparisonType).asAws, (_: StringCondition.StringComparisonType).asScala)
  }

  "Condition.NullCondition should" - {
    "have the correct comparison type" in {
      forAll { condition: Condition.NullCondition ⇒
        condition.comparisonType shouldBe "Null"
      }
    }

    "have the correct comparison values" in {
      forAll { condition: Condition.NullCondition ⇒
        condition.comparisonValues shouldBe Seq(condition.value.toString)
      }
    }

    "be extractable from its parts" in {
      forAll { condition: Condition.NullCondition ⇒
        inside((condition.key, condition.comparisonType, condition.comparisonValues)) {
          case Condition.NullCondition.fromParts(c) ⇒ c shouldBe condition
        }
      }
    }

    "not support values that are not a single boolean" in {
      forAll(
        arbitrary[Condition.NullCondition] → "baseCondition",
        nonSingularBooleanValues → "badValues"
      ) { (baseCondition, badValues) ⇒
        val parts: (String, String, Seq[String]) = (baseCondition.key, baseCondition.comparisonType, badValues)
        Condition.NullCondition.fromParts.unapply(parts) shouldBe None
      }
    }

    behave like multiValueSupportCondition[Condition.NullCondition]
  }

  "Condition.MultiValueCondition should" - {
    "have the correct key" in {
      forAll { condition: Condition.MultipleKeyValueCondition ⇒
        condition.key shouldBe condition.condition.key
      }
    }

    "have the correct comparison type" in {
      forAll { condition: Condition.MultipleKeyValueCondition ⇒
        val comparisonType = condition.op match {
          case Condition.SetOperation.ForAllValues ⇒ s"ForAllValues:${condition.condition.comparisonType}"
          case Condition.SetOperation.ForAnyValue  ⇒ s"ForAnyValue:${condition.condition.comparisonType}"
        }
        condition.comparisonType shouldBe comparisonType
      }
    }

    "have the correct comparison values" in {
      forAll { condition: Condition.MultipleKeyValueCondition ⇒
        condition.comparisonValues shouldBe condition.condition.comparisonValues
      }
    }

    "be extractable from its parts" in {
      forAll { condition: Condition.MultipleKeyValueCondition ⇒
        inside((condition.key, condition.comparisonType, condition.comparisonValues)) {
          case Condition.MultipleKeyValueCondition.fromParts(c) ⇒ c shouldBe condition
        }
      }
    }
  }

  //noinspection UnitMethodIsParameterless
  private def multiValueSupportCondition[T <: Condition with Condition.MultipleKeyValueSupport: Arbitrary]: Unit = {
    "support the forAllValues set operation" in {
      forAll { condition: T ⇒
        condition.forAllValues shouldBe
          Condition.MultipleKeyValueCondition(
            Condition.SetOperation.ForAllValues,
            condition)
      }
    }

    "support the forAnyValue set operation" in {
      forAll { condition: T ⇒
        condition.forAnyValue shouldBe
          Condition.MultipleKeyValueCondition(
            Condition.SetOperation.ForAnyValue,
            condition)
      }
    }
  }

  private val nonSingularBooleanValues: Gen[Seq[String]] = {
    val isBooleanString = Set("true", "false")
    arbitrary[Seq[Boolean]]
      .map(_.map(_.toString))
      .suchThat(vals ⇒ vals.size != 1 && vals.forall(isBooleanString))
  }
}
