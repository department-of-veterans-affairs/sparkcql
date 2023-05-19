package gov.va.sparkcql.model.fhir

import gov.va.sparkcql.model.fhir.Primitive._

trait NarrativeLike extends ElementLike {
  val status: Code
  val div: Xhtml
}

final case class Narrative (
  status: Code,
  div: Xhtml,
  id: Option[String] = None
) extends NarrativeLike