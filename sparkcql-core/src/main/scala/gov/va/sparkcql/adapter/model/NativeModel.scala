package gov.va.sparkcql.adapter.model

import scala.reflect.runtime.universe._
import gov.va.sparkcql.model.DataType
import gov.va.sparkcql.model.xsd.QName
import org.apache.spark.sql.types.StructType
import gov.va.sparkcql.model.CqlContent
import org.apache.spark.sql.Encoders
import gov.va.sparkcql.model.ValueSet
import gov.va.sparkcql.translation.cql2elm.CqlCompilerGateway
import gov.va.sparkcql.model.elm.VersionedIdentifier

class NativeModel extends ModelAdapter {

  override def toDataType[T: TypeTag](): DataType = {
    ???
  }

  override def toDataType(qname: QName): DataType = {
    ???
  }

  override def schema(dataType: DataType): Option[StructType] = {
    dataType match {
      case x if x == DataType[CqlContent]() => Some(Encoders.product[CqlContent].schema)
      case x if x == DataType[ValueSet]() => Some(Encoders.product[ValueSet].schema)
      case _ => None
    }
  }

  override def deserialize[T : TypeTag](data: String): Option[T] = {
    typeOf[T] match {
      case x if typeOf[T] <:< typeOf[CqlContent] =>
        val id = VersionedIdentifier(CqlCompilerGateway.parseVersionedIdentifier(data))
        Some(new CqlContent(id, data).asInstanceOf[T])
      case _ => None
    }
  }  

}
