package com.monsanto.arch.awsutil.testkit

import com.monsanto.arch.awsutil.config.Config
import org.scalacheck.{Arbitrary, Gen}
import org.scalacheck.Arbitrary.arbitrary

/**
  * Created by stevenkohner on 9/21/16.
  */
object ConfigGen {
  Config.init()

  def randomJsonParams: Gen[Map[String, Any]] =
    for {
      numElements <- Gen.choose(1, 256)
      ks <- Gen.listOfN(numElements, UtilGen.nonEmptyString)
      vs <- Gen.listOfN(numElements, jsonValue)
    } yield (ks zip vs).toMap

  def jsonValue: Gen[Any] = Gen.oneOf(arbitrary[String], arbitrary[Int], arbitrary[Double], arbitrary[String])
}
