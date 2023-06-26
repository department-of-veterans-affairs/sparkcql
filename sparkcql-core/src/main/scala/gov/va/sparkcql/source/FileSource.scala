package gov.va.sparkcql.source

import scala.reflect.runtime.universe._
import gov.va.sparkcql.io.Files
import org.json4s._
import org.json4s.jackson.Serialization.{read, write}
import javax.xml.namespace.QName
import org.apache.spark.sql.{SparkSession, DataFrame}
import gov.va.sparkcql.translator.cql2elm.CqlCompilerGateway
import gov.va.sparkcql.types._
import gov.va.sparkcql.logging.Log
import gov.va.sparkcql.model.Model

protected case class FileContent(path: String, value: String)

class FileSource(val models: List[Model], val spark: SparkSession, path: String) extends Source {

  assert(path != null)
  assert(path != "")

  private type JsonString = String

  val currentDir = Files.currentDir()
  
  lazy val fileContents = loadFiles(".cql") ++ loadFiles(".json")

  def loadFiles(ext: String): Seq[FileContent] = {
    Files.search(path, ext).map(c => {
      val ext = c.split("\\.").last
      FileContent(c.toLowerCase(), scala.io.Source.fromFile(c).mkString)
    }).toSeq
  }

  def isDataTypePresent(dataType: QName): Boolean = {
    if (fileContents.length == 0) {
      Log.warn(s"FileSourceAdapter found no eligible files at '${path}'.")
    }
    
    val found = fileContents.filter(content => {
      dataType match {
        case x if x == Model.toDataType[IdentifiedText]() && content.path.endsWith(".cql") =>
          true
        case x if x == Model.toDataType[ValueSet]() && content.path.endsWith(s".${x.getLocalPart.toLowerCase()}.json") =>
          true
        case _ => false
      }
    })
    found.length > 0
  }

  def acquireData(dataType: QName): Option[DataFrame] = {
    import spark.implicits._ 
    val x = this.path
    val jsonData = fileContents.flatMap(c => convert(dataType, c))
    if (jsonData.length > 0) {
      val schema = modelAggregate.schemaOf(dataType)
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
      case x if x == Model.toDataType[IdentifiedText]() && content.path.endsWith(".cql") =>
        // TODO: Refactor to move this logic into separate serialization system. Adapters shouldn't
        // need to know how to deserialize.
        val id = Identifier(CqlCompilerGateway.parseVersionedIdentifier(content.value))
        val entity = new IdentifiedText(id, content.value)
        implicit val formats: Formats = DefaultFormats
        Some(write(entity))
      case x if x == Model.toDataType[ValueSet]() && content.path.endsWith(s".${x.getLocalPart.toLowerCase()}.json") =>
        Some(content.value)
      case _ => None
    }
  }
}