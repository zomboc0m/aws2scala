package com.monsanto.arch.awsutil.config.model

import com.amazonaws.services.config.{model => aws}
import com.monsanto.arch.awsutil.converters.ConfigConverters._
import org.scalatest.prop.TableDrivenPropertyChecks._
import org.scalatest.FreeSpec
import org.scalatest.Matchers._

class EventSourceSpec extends FreeSpec {
  val sources = Table("Event Sources", EventSource.values: _*)
  "an EventSource" - {
    "can be round-tripped" - {
      "from its AWS equivalent" in {
        forAll(sources) { s ⇒
          val es = aws.EventSource.fromValue(s.name)

          es.asScala.asAws shouldBe es
        }
      }

      "via its AWS equivalent" in {
        forAll(sources) { es ⇒
          es.asAws.asScala shouldBe es
        }
      }
    }

    "has a toString that matches the ID" in {
      forAll(sources) { es ⇒
        es.toString shouldBe es.name
      }
    }

    "can be round-tripped via its string representation" in {
      forAll(sources) { es ⇒
        EventSource.unapply(es.name) shouldBe Some(es)
      }
    }

    "can be arbitrarily generated" in {
      forAll(sources) { es ⇒
        EventSource.values.contains(es)
      }
    }
  }
}

