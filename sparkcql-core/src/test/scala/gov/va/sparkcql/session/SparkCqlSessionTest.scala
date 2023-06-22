package gov.va.sparkcql.session

import gov.va.sparkcql.TestBase
import collection.JavaConverters._
import org.apache.spark.sql.SparkSession
import gov.va.sparkcql.types._
import gov.va.sparkcql.logging.Log
import gov.va.sparkcql.source.FileSourceConfiguration

class SparkCqlSessionTest extends TestBase {

  val sparkcql = {
    SparkCqlSession.build(spark)
      .withConfig(FileSourceConfiguration("./src/test/resources/cql"))
      .create()
  }

  "A SparkCqlSession" should "should not be creatable outside of builder" in {
    assertDoesNotCompile("val builder = new SparkCqlSession.Builder(null)")
    assertDoesNotCompile("val sparkcql = new SparkCqlSession(null)")
  }

  it should "support statically typed retrievals" in {
    assert(sparkcql.retrieve[IdentifiedText].isDefined)
  }

  it should "support dynamically typed retrievals" in {
    // val df = sparkcql.retrieve(FhirDataType("Encounter"))
    // assert(df.get.head.getAs[String]("status") != null)
  }
}