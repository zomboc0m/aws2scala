package com.monsanto.arch.awsutil.config.model

import com.amazonaws.services.config.{model => aws}
import com.monsanto.arch.awsutil.converters.ConfigConverters._
import com.monsanto.arch.awsutil.testkit.ConfigScalaCheckImplicits._
import org.scalatest.FreeSpec
import org.scalatest.Matchers._
import org.scalatest.prop.GeneratorDrivenPropertyChecks._

class ScopeSpec extends FreeSpec {
  "a Scope can be round-tripped" - {
    "from its AWS equivalent" in {
      forAll { scope: Scope ⇒
        val awsScope = new aws.Scope()
        scope.resourceId.foreach(awsScope.withComplianceResourceId)
        scope.resourceTypes.foreach( rt ⇒ awsScope.withComplianceResourceTypes(rt.map(_.name): _*))
        scope.tag.foreach { case (k, v) => awsScope.withTagKey(k); awsScope.withTagValue(v) }

        awsScope.asScala.asAws shouldBe awsScope
      }
    }

    "via its AWS equivalent" in {
      forAll { scope: Scope ⇒
        scope.asAws.asScala shouldBe scope
      }
    }
  }
}
