package gov.va.sparkcql.core.adapter.source

import scala.reflect.runtime.universe._
import gov.va.sparkcql.core.io.Files
import org.apache.spark.sql.{SparkSession, Dataset, Row, Encoders}
import gov.va.sparkcql.core.translation.cql2elm.CqlCompilerGateway
import gov.va.sparkcql.core.model.{DataType, ValueSet, CqlContent}
import org.json4s._
import org.json4s.jackson.Serialization.{read, write}
import gov.va.sparkcql.core.adapter.model.ModelAdapter
import gov.va.sparkcql.core.Log
import gov.va.sparkcql.core.model.VersionedId
import javax.xml.namespace.QName

protected case class FileContent(path: String, value: String)

class FileSourceAdapter(val modelAdapter: ModelAdapter, val spark: SparkSession, path: String) extends SourceAdapter {

  assert(path != null)
  assert(path != "")

  private type JsonString = String

  val currentDir = Files.currentDir()
  
  lazy val fileContents = Files.search(path, "*").map(c => {
    val ext = c.split("\\.").last
    FileContent(c.toLowerCase(), scala.io.Source.fromFile(c).mkString)
  }).toSeq

  def acquireData(dataType: QName): Option[Dataset[Row]] = {
    import spark.implicits._ 
    val x = this.path
    val jsonData = fileContents.flatMap(c => convert(dataType, c))
    if (jsonData.length > 0) {
      val schema = modelAdapter.schemaOf(dataType)
      if (schema.isDefined) {
        Some(spark.read.schema(schema.get).json(jsonData.toDS()))
      } else {
        Log.error(s"No schema found for ${dataType.toString()}. Attempting to infer schema.")
        Some(spark.read.json(jsonData.toDS()))
        // TODO: Should we allow inferred schemas?
      }
    } else {
      None
    }
  }

  def convert(dataType: QName, content: FileContent): Option[JsonString] = {
    dataType match {
      case x if x == DataType[CqlContent]() && content.path.endsWith(".cql") =>
        // TODO: Refactor to move this logic into separate serialization system. Adapters shouldn't
        // need to know how to deserialize.
        val id = VersionedId(CqlCompilerGateway.parseVersionedIdentifier(content.value))
        val entity = new CqlContent(id, content.value)
        implicit val formats: Formats = DefaultFormats
        Some(write(entity))
      case x if x == DataType[ValueSet]() && content.path.endsWith(s".${x.getLocalPart.toLowerCase()}.json") =>
        Some(content.value)
      case _ => None
    }
  }
}