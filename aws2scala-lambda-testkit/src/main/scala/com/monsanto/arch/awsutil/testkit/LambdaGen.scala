package com.monsanto.arch.awsutil.testkit

import com.monsanto.arch.awsutil.testkit.UtilGen._
import org.scalacheck.Gen

object LambdaGen {
  val functionName = stringOf(wordChar,1,140)

  def nLengthNumString(n: Int) = Gen.listOfN(n, Gen.oneOf('0' to '9')).map(_.mkString)

  val versionChar: Gen[Char] = Gen.oneOf(('0' to '9') :+ '.')

  val versionNum: Gen[String] = stringOf(versionChar,1,1024).suchThat(_.head != '.')

  //Example timestamp 2016-07-28T14:20:24.314+0000
  //regex "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{3}\\+0000$"
  val awsTimestamp: Gen[String] =
    for {
      year <- nLengthNumString(4)
      month <- nLengthNumString(2)
      day <- nLengthNumString(2)
      hour <- nLengthNumString(2)
      minute <- nLengthNumString(2)
      second <- nLengthNumString(2)
      fracsec <- nLengthNumString(3)
    } yield {
      s"""$year-$month-${day}T$hour:$minute:$second.$fracsec+0000"""
    }
}
