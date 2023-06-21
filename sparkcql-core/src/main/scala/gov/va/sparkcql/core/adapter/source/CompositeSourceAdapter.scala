package gov.va.sparkcql.core.adapter.source

import scala.reflect.runtime.universe._
import org.apache.spark.sql.{SparkSession, Dataset, Row}
import org.apache.spark.sql.types.StructType
import gov.va.sparkcql.core.adapter.model.CompositeModelAdapter
import gov.va.sparkcql.core.Log
import javax.xml.namespace.QName
import gov.va.sparkcql.core.adapter.{Composite}

class CompositeSourceAdapter(adapters: List[SourceAdapter]) extends Composite with SourceAdapter {

  val spark: SparkSession = null

  def isDefaultConfigurable(): Boolean = true
  
  def isDataTypePresent(dataType: QName): Boolean = {
    adapters.filter(_.isDataTypePresent(dataType)).length > 0
  }
  
  def acquireData(dataType: QName): Option[Dataset[Row]] = {
    val eligibleAdapters = adapters.filter(_.isDataTypePresent(dataType))
    if (!eligibleAdapters.isEmpty) {
      val acquiredDf = eligibleAdapters.flatMap(a => {
        val df = a.acquireData(dataType)
        if (df.isEmpty) {
          Log.warn(s"${a.getClass().getSimpleName()} stated support for data type '${dataType.toString()}' but none was found.")
          None
        } else if (df.get.schema.fields.length == 0) {
          Log.warn(s"${a.getClass().getSimpleName()} returned a columnless dataframe when None should have been returned. Ignoring output.")
          None
        } else {
          df
        }
      })
      Some(acquiredDf.reduce(_.union(_)))   // union all dataframes
    } else {
      Log.warn(s"Attempted to acquire missing data for type '${dataType.toString()}' without verifying data was present.")
      None
    }
  }
}