package com.monsanto.arch.awsutil.auth.policy

import java.nio.ByteBuffer
import java.util.Date

import akka.util.ByteString

sealed trait Condition

object Condition {
  /** Allows creation of an ARN condition using the given key.
    *
    * {{{
    * // Matches "some-key" (if it exists) and any SQS queue from any region with the account 1234567890
    * Condition.key("some-key").ifExists.isLike("arn:aws:sqs:*:123456789012:*")
    *
    * // Matches a source ARN that matches the given ARN
    * Condition.sourceArn is "arn:aws:sqs:us-east-1:123556789012:MyQueue"
    * }}}
    */
  def arn(key: String): ArnKey = ArnKey(key, ignoreMissing = false)

  /** Creates a condition that matches a binary key value. */
  def binary(key: String): BinaryKey = BinaryKey(key, ignoreMissing = false)

  /** This condition indicates the source resource that is modifying another resource. */
  def sourceArn: ArnKey = ArnKey("aws:SourceArn", ignoreMissing = false)

  /** Creates a condition that checks if the given key is true. */
  def isTrue(key: String): BooleanCondition = BooleanCondition(key, value = true, ignoreMissing = false)

  /** Creates a condition that checks if the given key is true. */
  def isFalse(key: String): BooleanCondition = BooleanCondition(key, value = false, ignoreMissing = false)

  /** Allows creation of date conditions using the give key. */
  def date(key: String): DateKey = DateKey(key, ignoreMissing = false)

  /** Allows creation of date conditions using the current time. */
  def currentTime: DateKey = DateKey("aws:CurrentTime", ignoreMissing = false)

  /** Allows creation of date conditions using the epoch time. */
  def epochTime: DateKey = DateKey("aws:EpochTime", ignoreMissing = false)

  /** Allows creation of IP address conditions using the given key. */
  def ipAddress(key: String): IpAddressKey = IpAddressKey(key, ignoreMissing = false)

  /** Allows creation of IP address conditions using the source IP address of the request. */
  def sourceIp: IpAddressKey = IpAddressKey("aws:SourceIp", ignoreMissing = false)

  /** Allows creation of keys supporting numeric comparison conditions. */
  def numeric(key: String): NumericKey = NumericKey(key, ignoreMissing = false)

  case class ArnKey private[Condition] (key: String, ignoreMissing: Boolean) {
    /** Generates a condition for when the ARN is exactly equal to the given value. */
    def is(comparisonValues: String*): ArnCondition =
      ArnCondition(key, ArnComparisonType.Equals, comparisonValues, ignoreMissing)

    /** Generates a condition for when the ARN is a loose case-insensitive match to the given value. */
    def isLike(comparisonValues: String*): ArnCondition =
      ArnCondition(key, ArnComparisonType.Like, comparisonValues, ignoreMissing)

    /** Generates a condition for when the ARN is exactly not equal to the given value. */
    def isNot(comparisonValues: String*): ArnCondition =
      ArnCondition(key, ArnComparisonType.NotEquals, comparisonValues, ignoreMissing)

    /** Generates a condition for when the ARN is not a loose case-insensitive match to the given value. */
    def isNotLike(comparisonValues: String*): ArnCondition =
      ArnCondition(key, ArnComparisonType.NotLike, comparisonValues, ignoreMissing)

    /** Makes the resulting condition be ignored if the given key is missing. */
    def ifExists: ArnKey = ArnKey(key, ignoreMissing = true)
  }

  sealed trait ArnComparisonType
  object ArnComparisonType {
    /** Exact matching. */
    case object Equals extends ArnComparisonType
    /** Loose case-insensitive matching of the ARN. */
    case object Like extends ArnComparisonType
    /** Negated form of [[Equals]]. */
    case object NotEquals extends ArnComparisonType
    /** Negated form of [[Like]]. */
    case object NotLike extends ArnComparisonType

    val values: Seq[ArnComparisonType] = Seq(Equals, Like, NotEquals, NotLike)
  }

  case class ArnCondition(key: String,
                          comparisonType: ArnComparisonType,
                          values: Seq[String],
                          ignoreMissing: Boolean) extends Condition {
    /** Creates a copy of this condition that will ignore a missing key in a request. */
    def ifExists: ArnCondition = copy(ignoreMissing = true)
  }

  case class BinaryKey private[Condition] (key: String, ignoreMissing: Boolean) {
    /** Creates a condition to match the given binary value. */
    def is(value: ByteString): BinaryCondition = BinaryCondition(key, Seq(value), ignoreMissing)

    /** Creates a condition to match the given binary value. */
    def is(value: Array[Byte]): BinaryCondition = BinaryCondition(key, Seq(ByteString(value)), ignoreMissing)

    /** Creates a condition to match the given binary value. */
    def is(value: ByteBuffer): BinaryCondition = BinaryCondition(key, Seq(ByteString(value.duplicate())), ignoreMissing)

    /** Makes the resulting condition be ignored if the given key is missing. */
    def ifExists: BinaryKey = copy(ignoreMissing = true)
  }

  case class BinaryCondition(key: String, values: Seq[ByteString], ignoreMissing: Boolean) extends Condition {
    /** Creates a copy of this condition that will ignore a missing key in a request. */
    def ifExists: BinaryCondition = copy(ignoreMissing = true)
  }

  case class BooleanCondition(key: String,
                              value: Boolean,
                              ignoreMissing: Boolean) extends Condition {
    /** Creates a copy of this condition that will ignore a missing key in a request. */
    def ifExists: BooleanCondition = copy(ignoreMissing = true)
  }

  sealed trait DateComparisonType
  object DateComparisonType {
    case object Equals extends DateComparisonType
    case object After extends DateComparisonType
    case object AtOrAfter extends DateComparisonType
    case object Before extends DateComparisonType
    case object AtOrBefore extends DateComparisonType
    case object NotEquals extends DateComparisonType

    val values: Seq[DateComparisonType] = Seq(Equals, After, AtOrAfter, Before, AtOrBefore, NotEquals)
  }

  case class DateCondition(key: String,
                           comparisonType: DateComparisonType,
                           values: Seq[Date],
                           ignoreMissing: Boolean) extends Condition {
    /** Creates a copy of this condition that will ignore a missing key in a request. */
    def ifExists: DateCondition = copy(ignoreMissing = true)
  }

  case class DateKey private[Condition] (key: String, ignoreMissing: Boolean) {
    /** Matches a specific date. */
    def is(dates: Date*): DateCondition = DateCondition(key, DateComparisonType.Equals, dates, ignoreMissing)

    /** Negated matching. */
    def isNot(dates: Date*): DateCondition = DateCondition(key, DateComparisonType.NotEquals, dates, ignoreMissing)

    /** Matching before a specific date and time. */
    def isBefore(dates: Date*): DateCondition = DateCondition(key, DateComparisonType.Before, dates, ignoreMissing)

    /** Matching at or before a specific date and time. */
    def isAtOrBefore(dates: Date*): DateCondition = DateCondition(key, DateComparisonType.AtOrBefore, dates, ignoreMissing)

    /** Matching after a specific date and time. */
    def isAfter(dates: Date*): DateCondition = DateCondition(key, DateComparisonType.After, dates, ignoreMissing)

    /** Matching at or after a specific date and time. */
    def isAtOrAfter(dates: Date*): DateCondition = DateCondition(key, DateComparisonType.AtOrAfter, dates, ignoreMissing)

    /** Makes the resulting condition be ignored if the given key is missing. */
    def ifExists: DateKey = copy(ignoreMissing = true)
  }

  sealed trait IpAddressComparisonType
  object IpAddressComparisonType {
    /** Matches an IP address against a CIDR IP range, evaluating to true if the IP address being tested is in the
      * conditions‘s specified CIDR IP range.
      */
    case object IsIn extends IpAddressComparisonType
    /** Negated form of [[IsIn]]. */
    case object IsNotIn extends IpAddressComparisonType

    val values: Seq[IpAddressComparisonType] = Seq(IsIn, IsNotIn)
  }

  case class IpAddressKey private[Condition] (key: String, ignoreMissing: Boolean) {
    /** Creates a condition that will match if the tested address is within one of the given ranges. */
    def isIn(cidrBlocks: String*): IpAddressCondition = IpAddressCondition(key, IpAddressComparisonType.IsIn, cidrBlocks, ignoreMissing)

    /** Creates a condition that will match if the tested address is not within one of the given ranges. */
    def isNotIn(cidrBlocks: String*): IpAddressCondition = IpAddressCondition(key, IpAddressComparisonType.IsNotIn, cidrBlocks, ignoreMissing)

    /** Makes the resulting condition be ignored if the given key is missing. */
    def ifExists: IpAddressKey = copy(ignoreMissing = true)
  }

  case class IpAddressCondition(key: String,
                                comparisonType: IpAddressComparisonType,
                                cidrBlocks: Seq[String],
                                ignoreMissing: Boolean) extends Condition {
    /** Creates a copy of this condition that will ignore a missing key in a request. */
    def ifExists: IpAddressCondition = copy(ignoreMissing = true)
  }

  sealed trait NumericComparisonType
  object NumericComparisonType {
    case object Equals extends NumericComparisonType
    case object GreaterThan extends NumericComparisonType
    case object GreaterThanEquals extends NumericComparisonType
    case object LessThan extends NumericComparisonType
    case object LessThanEquals extends NumericComparisonType
    case object NotEquals extends NumericComparisonType

    val values: Seq[NumericComparisonType] =
      Seq(Equals, GreaterThan, GreaterThanEquals, LessThan, LessThanEquals, NotEquals)
  }

  case class NumericCondition(key: String,
                           comparisonType: NumericComparisonType,
                           values: Seq[Double],
                           ignoreMissing: Boolean) extends Condition {
    /** Creates a copy of this condition that will ignore a missing key in a request. */
    def ifExists: NumericCondition = copy(ignoreMissing = true)
  }

  case class NumericKey private[Condition] (key: String, ignoreMissing: Boolean) {
    /** Matches a specific date. */
    def is(values: Double*): NumericCondition = NumericCondition(key, NumericComparisonType.Equals, values, ignoreMissing)

    /** Matching before a specific date and time. */
    def isLessThan(values: Double*): NumericCondition = NumericCondition(key, NumericComparisonType.LessThan, values, ignoreMissing)

    /** Matching at or before a specific date and time. */
    def isLessThanOrEqualTo(values: Double*): NumericCondition = NumericCondition(key, NumericComparisonType.LessThanEquals, values, ignoreMissing)

    /** Matching after a specific date and time. */
    def isGreaterThan(values: Double*): NumericCondition = NumericCondition(key, NumericComparisonType.GreaterThan, values, ignoreMissing)

    /** Matching at or after a specific date and time. */
    def isGreaterThanOrEqualTo(values: Double*): NumericCondition = NumericCondition(key, NumericComparisonType.GreaterThanEquals, values, ignoreMissing)

    /** Negated matching. */
    def isNot(values: Double*): NumericCondition = NumericCondition(key, NumericComparisonType.NotEquals, values, ignoreMissing)

    /** Makes the resulting condition be ignored if the given key is missing. */
    def ifExists: NumericKey = copy(ignoreMissing = true)
  }

  trait StringComparisonType
  object StringComparisonType {
    /** Exact matching, case sensitive. */
    case object Equals extends StringComparisonType

    /** Negated form of [[Equals]]. */
    case object NotEquals extends StringComparisonType

    /** Exact matching, ignoring case. */
    case object EqualsIgnoreCase extends StringComparisonType

    /** Negated form of [[NotEquals]]. */
    case object NotEqualsIgnoreCase extends StringComparisonType

    /** Case-sensitive matching.  The values can include a multi-character match wildcard (`*`) or a single-character
      * match wildcard (?) anywhere in the string.
      */
    case object Like extends StringComparisonType
  }

  // String

  // existence
}
