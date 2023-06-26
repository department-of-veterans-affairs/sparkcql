package gov.va.sparkcql.synthea

import gov.va.sparkcql.TestBase
import org.apache.spark.sql.{SparkSession, DataFrame}
import org.apache.spark.sql.functions._
import org.hl7.elm.r1.Code
import gov.va.sparkcql.session.SparkCqlSession
import gov.va.sparkcql.fhir.FhirDataType
import gov.va.sparkcql.model.DataType

class SyntheaSourceAdapterTest extends TestBase {
  
  val sparkcql = {
    SparkCqlSession.build(spark)
      .withConfig(SyntheaSourceConfiguration(PopulationSize.PopulationSize10))
      .create()
  }

  "A SyntheaSourceAdapter" should "dynamically retrieve resources" in {
    assert(sparkcql.retrieve(FhirDataType("Encounter")).get.head().getAs[String]("status") != null)
    assert(sparkcql.retrieve(FhirDataType("Condition")).get.head().getAs[String]("id") != null)
  }

  "A SyntheaDataProvider" should "lazily initialize" in {
    // val provider = new SyntheaDataProvider(PopulationSize.PopulationSize10)
    // assert(
    //   provider.fetch[] .fetch(spark, new Code().withCode("Patient"), None).get
    //     .distinct.count() == 10
    // )
  }

  it should "return exactly 10 bundles when using PopulationSize10" in {
  }

  it should "return None for non-FHIR data types" in {
    assert(sparkcql.retrieve(DataType("http://example.com", "NotFhir")).isEmpty)
  }

  // it should "retrieve encounters when using PopulationSize10" in {
  //   val provider = new FhirSyntheticClinicalDataProvider(FhirSyntheticClinicalDataProvider.PopulationSize10)
  //   assert(
  //     provider10.retrieve(spark, new Code().withCode("Encounter"), None).get
  //       .count() > 100
  //   )
  // }

  // it should "should lazily load" in {
  //   val someNumberBetweenAllPossibleAndLoadedTypes = 5
  //   assert(spark.sql("SHOW TABLES").count() < someNumberBetweenAllPossibleAndLoadedTypes)
  // }
}