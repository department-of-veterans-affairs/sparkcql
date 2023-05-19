package gov.va.sparkcql.model.fhir

import gov.va.sparkcql.model.fhir.Primitive._

trait DomainResourceLike extends ResourceLike {
  val text: Option[Narrative]
  val contained: Option[List[Resource]]
}

final case class DomainResource (
  id: Id,
  resourceType: Option[String],
  meta: Option[Meta] = None,
  implicitRules: Option[Uri] = None,
  language: Option[Code] = None,
  text: Option[Narrative] = None,
  contained: Option[List[Resource]] = None
) extends DomainResourceLike