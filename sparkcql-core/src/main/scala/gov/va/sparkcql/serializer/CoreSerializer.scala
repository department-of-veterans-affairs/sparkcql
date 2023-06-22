//package gov.va.sparkcql.serializer

// import scala.reflect.runtime.universe._
// import org.apache.spark.sql.types.StructType
// import gov.va.sparkcql.model.IdentifiedText
// import org.apache.spark.sql.Encoders
// import gov.va.sparkcql.model.ValueSet
// import gov.va.sparkcql.translator.cql2elm.CqlCompilerGateway
// import gov.va.sparkcql.model.VersionedId

// class CoreSerializer {

//   override def deserialize[T : TypeTag](data: String): Option[T] = {
//     typeOf[T] match {
//       case x if typeOf[T] <:< typeOf[IdentifiedText] =>
//         val id = VersionedId(CqlCompilerGateway.parseVersionedIdentifier(data))
//         Some(new IdentifiedText(id, data).asInstanceOf[T])
//       case _ => None
//     }
//   }

// }
