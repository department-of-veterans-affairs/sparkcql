package gov.va.sparkcql.core.adapter.source

import scala.reflect.runtime.universe._
import gov.va.sparkcql.TestBase
import org.hl7.elm.r1.VersionedIdentifier
import org.apache.spark.sql.{Dataset, Encoders}
import org.apache.spark.sql.functions._
import gov.va.sparkcql.core.model.{DataType, ValueSet, CqlContent}
import gov.va.sparkcql.core.adapter.model.NativeModel
import gov.va.sparkcql.core.adapter.model.ModelAdapter

class FileSourceAdapterTest extends TestBase {
  
  import spark.implicits._
  lazy val model = new NativeModel().create()
  lazy val source = FileSource("./src/test/resources").create(spark, model)

  "A FileSourceAdapter" should "load and map CQL files (typed)" in {
    assert(source.acquireData[CqlContent]().get.filter(_.identifier.id == "BasicRetrieve").count() == 1)
  }

  it should "load and map valuesets (typed)" in {
    assert(source.acquireData[ValueSet]().get.filter(_.name == "Emergency Department Visit").count() == 1)
  }

  it should "load valuesets based on DataType reference" in {
    val dataType = DataType[ValueSet]()
    assert(source.acquireData(dataType).get.filter(col("name").equalTo("Emergency Department Visit")).count() == 1)
  }

  it should "read empty gracefully" in {
    lazy val emptySource = FileSource("./src/doesnotexist").create(spark, model)
    assert(emptySource.acquireData(DataType[ValueSet]()).isEmpty)
  }

}