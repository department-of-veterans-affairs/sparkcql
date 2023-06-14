// package gov.va.sparkcql.evaluator

// import gov.va.sparkcql.TestBase
// import collection.JavaConverters._
// import java.io.StringWriter
// import org.apache.spark.sql.SparkSession
// import org.apache.spark.sql.functions._
// import gov.va.sparkcql.core.model.fhir.r4._
// import gov.va.sparkcql.core.model.elm.ElmTypes
// import gov.va.sparkcql.core.translation.cql2elm.CqlToElmTranslator
// import gov.va.sparkcql.core.translation.elm2spark.{ElmToSparkTranslator, ElmDatasetLink}
// import org.cqframework.cql.cql2elm.LibraryContentType
// import org.cqframework.cql.elm.serializing.ElmLibraryWriterFactory
// import java.io.{File, FileWriter}
// import org.hl7.elm.r1.Library

// class ElmToSparkTranslatorTest extends TestBase {

//   lazy val cqlToElm = new CqlToElmTranslator()
//   lazy val sourceAdapter = SyntheaDataProvider(PopulationSize.PopulationSize10)
//   lazy val clinicalDataAdapter: Option[DataAdapter] = Some(clinicalDataProvider.createAdapter(spark))
//   lazy val terminologyDataAdapter: Option[DataAdapter] = None
//   lazy val elmToSpark = new ElmToSparkTranslator(spark, clinicalDataAdapter, terminologyDataAdapter)

//   // "An ElmToSparkTranslator" should "should retrieve Encounter data" in {
//   //   val compilation = compiler.compile(List("using QUICK \n define testEnc: [Encounter]"))
//   //   val evaluation: Evaluation = evaluator.eval(compilation)
//   // }

//   // it should "should execute multiple retrieves" in {
//   //   val compilation = compiler.compile(List("using QUICK \n define testEncounter: [Encounter] \n define testCondition: [Condition]"))
//   //   val evaluation: Evaluation = evaluator.eval(compilation)
//   // }

//   it should "filter based on MeasurementPeriod parameter" in {
//     val evaluator = new ElmToSparkTranslator(spark, clinicalDataAdapter, terminologyDataAdapter)
//     val libraries = cqlToElm.translate(List("""
//       library Test
//       using QUICK
//       parameter MeasurementPeriod Interval<Date>
//       define "Encounter A":
//           [Encounter] E
//           where E.period ends during MeasurementPeriod"""
//       ))
//     writeElm(libraries)
//     assertNoElmErrors(libraries)
//     val parameters = Some(Map("MeasurementPeriod" -> ElmTypes.DateInterval(ElmTypes.Date("2013-01-01"), ElmTypes.Date("2014-01-01"), true, false)))
//     val evaluation = elmToSpark.translate(parameters, libraries)
//     showData(evaluation)
//   }

//   def showData(dataLinks: Seq[ElmDatasetLink]): Unit = {
//     dataLinks.map(_.result.show())
//   }

//   def writeElm(libraries: Seq[Library]): Unit = {
//     libraries.map(library => {
//       val name = if (library.getIdentifier().getId() != null) { library.getIdentifier().getId() } else { java.util.UUID.randomUUID.toString }
//       val writer = new FileWriter(new File(name + ".json"))
//       ElmLibraryWriterFactory.getWriter(LibraryContentType.JSON.mimeType()).write(library, writer)
//     })
//   }
// }