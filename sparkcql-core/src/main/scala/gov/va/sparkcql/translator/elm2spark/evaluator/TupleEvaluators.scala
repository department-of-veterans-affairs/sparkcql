package gov.va.sparkcql.translator.elm2spark.evaluator

import scala.collection.JavaConverters._
import org.hl7.elm.{r1 => elm}
import org.apache.spark.sql.Column
import org.apache.spark.sql.functions._

class TupleEvaluator(val element: elm.Tuple) extends Evaluator {

  override protected def resolveChildren(): List[Object] = element.getElement().asScala.toList

  override def evaluate(context: Context): Object = {
    val cols = children.map(_.evaluate(context).asInstanceOf[Column])
    struct(cols:_*)
  }
}

class TupleElementEvaluator(val element: elm.TupleElement) extends Evaluator {

  override protected def resolveChildren(): List[Object] = List(element.getValue())

  override def evaluate(context: Context): Object = {
    assert(children.length == 1)
    val value = children.head.evaluate(context).asInstanceOf[Column]
    value.alias(element.getName())
  }
}