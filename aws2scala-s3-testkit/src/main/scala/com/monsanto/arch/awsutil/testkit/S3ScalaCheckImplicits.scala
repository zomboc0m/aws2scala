package com.monsanto.arch.awsutil.testkit

import java.util.Date

import com.monsanto.arch.awsutil.s3.model._
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen, Shrink}

object S3ScalaCheckImplicits {
  implicit lazy val arbBucket: Arbitrary[Bucket] =
    Arbitrary {
      for {
        name ← S3Gen.bucketName
        owner ← arbitrary[Owner]
        creationDate ← arbitrary[Date]
      } yield Bucket(name, owner, creationDate)
    }

  implicit lazy val shrinkBucket: Shrink[Bucket] =
    Shrink { bucket ⇒
      Shrink.shrink(bucket.name).filter(Bucket.validName).map(n ⇒ bucket.copy(name = n)) append
        Shrink.shrink(bucket.owner).map(o ⇒ bucket.copy(owner = o)) append
        Shrink.shrink(bucket.creationDate).map(d ⇒ bucket.copy(creationDate = d))
    }

  implicit lazy val arbOwner: Arbitrary[Owner] =
    Arbitrary {
      for {
        id ← S3Gen.canonicalIdentifier
        displayName ← UtilGen.nonEmptyString
      } yield Owner(id, displayName)
    }

  implicit lazy val shrinkOwner: Shrink[Owner] =
    Shrink { owner ⇒
      Shrink.shrink(owner.displayName).filter(_.nonEmpty).map(x ⇒ owner.copy(displayName = x))
    }

  implicit lazy val arbRegion: Arbitrary[Region] = Arbitrary(Gen.oneOf(Region.values))

  implicit lazy val arbCreateBucketRequest: Arbitrary[CreateBucketRequest] =
    Arbitrary {
      for {
        name ← S3Gen.bucketName
        region ← arbitrary[Option[Region]]
        acl ← arbitrary[Option[Either[CannedAccessControlList,Seq[Grant]]]]
      } yield {
        acl match {
          case None ⇒
            CreateBucketRequest.CreateBucketWithNoAcl(name, region)
          case Some(Left(cannedAcl)) ⇒
            CreateBucketRequest.CreateBucketWithCannedAcl(name, cannedAcl, region)
          case Some(Right(grants)) ⇒
            CreateBucketRequest.CreateBucketWithGrants(name, grants, region)
        }
      }
    }

  implicit lazy val shrinkCreateBucketRequest: Shrink[CreateBucketRequest] =
    Shrink {
      case r @ CreateBucketRequest.CreateBucketWithNoAcl(name, region) ⇒
        Shrink.shrink(name).filter(Bucket.validName).map(n ⇒ r.copy(bucketName = n)) append
          Shrink.shrink(region).map(x ⇒ r.copy(region = x))
      case r @ CreateBucketRequest.CreateBucketWithCannedAcl(name, _, region) ⇒
        Shrink.shrink(name).filter(Bucket.validName).map(n ⇒ r.copy(bucketName = n)) append
          Shrink.shrink(region).map(x ⇒ r.copy(region = x))
      case r @ CreateBucketRequest.CreateBucketWithGrants(name, grants, region) ⇒
        Shrink.shrink(name).filter(Bucket.validName).map(n ⇒ r.copy(bucketName = n)) append
          Shrink.shrink(grants).map(g ⇒ r.copy(grants = g)) append
          Shrink.shrink(region).map(x ⇒ r.copy(region = x))
    }

  implicit lazy val arbGrant: Arbitrary[Grant] = Arbitrary(Gen.resultOf(Grant.apply _))

  implicit lazy val shrinkGrant: Shrink[Grant] = Shrink.xmap((Grant.apply _).tupled, Grant.unapply(_).get)

  implicit lazy val arbGrantee: Arbitrary[Grantee] =
    Arbitrary {
      Gen.oneOf(
        arbitrary[Grantee.Canonical],
        arbitrary[Grantee.EmailAddress],
        Gen.oneOf(Grantee.AllUsers, Grantee.AuthenticatedUsers, Grantee.LogDelivery))
    }

  implicit lazy val shrinkGrantee: Shrink[Grantee] =
    Shrink {
      case c: Grantee.Canonical ⇒ Shrink.shrink(c)
      case e: Grantee.EmailAddress ⇒ Shrink.shrink(e)
      case g: Grantee.GroupGrantee ⇒ Stream.empty
    }

  implicit lazy val arbGranteeCanonical: Arbitrary[Grantee.Canonical] =
    Arbitrary {
      for {
        id ← S3Gen.canonicalIdentifier
        displayName ← arbitrary[Option[String]]
      } yield Grantee.Canonical(id, displayName)
    }

  implicit lazy val shrinkGranteeCanonical: Shrink[Grantee.Canonical] =
    Shrink { grantee ⇒
      Shrink.shrink(grantee.displayName).map(x ⇒ grantee.copy(displayName = x))
    }

  implicit lazy val arbGranteeEmailAddress: Arbitrary[Grantee.EmailAddress] =
    Arbitrary(UtilGen.emailAddress.map(Grantee.EmailAddress))

  implicit lazy val shrinkGranteeEmailAddress: Shrink[Grantee.EmailAddress] =
    Shrink { grantee ⇒
      Shrink.shrink(grantee.emailAddress).filter(_.matches("^.+@example.com$")).map(x ⇒ grantee.copy(emailAddress = x))
    }

  implicit lazy val arbPermission: Arbitrary[Permission] = Arbitrary(Gen.oneOf(Permission.values))

  implicit lazy val arbCannedAccessControlList: Arbitrary[CannedAccessControlList] =
    Arbitrary(Gen.oneOf(CannedAccessControlList.values))
}
