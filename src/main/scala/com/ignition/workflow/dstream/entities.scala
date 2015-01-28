package com.ignition.workflow.dstream

import scala.reflect.ClassTag

import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.DStream

import com.ignition.workflow.{ Step0, Step1, Step2, StepN }

/**
 * Creates a new DStream[T]
 */
abstract class DStreamStep0[T: ClassTag](func: StreamingContext => DStream[T])
  extends Step0[DStream[T], StreamingContext](func)

/**
 * Transforms DStream[S] => DStream[T].
 *
 * @author Vlad Orzhekhovskiy
 */
abstract class DStreamStep1[S: ClassTag, T: ClassTag](func: DStream[S] => DStream[T])
  extends Step1[DStream[S], DStream[T], StreamingContext](_ => func)

/**
 * Transforms (DStream[S1], DStream[S2]) => DStream[T].
 *
 * @author Vlad Orzhekhovskiy
 */
abstract class DStreamStep2[S1: ClassTag, S2: ClassTag, T: ClassTag](func: (DStream[S1], DStream[S2]) => DStream[T])
  extends Step2[DStream[S1], DStream[S2], DStream[T], StreamingContext](_ => func)

/**
 * Transforms StreamingContext => Iterable[DStream[S]] => DStream[T].
 *
 * @author Vlad Orzhekhovskiy
 */
abstract class DStreamStepN[S: ClassTag, T: ClassTag](func: StreamingContext => Iterable[DStream[S]] => DStream[T])
  extends StepN[DStream[S], DStream[T], StreamingContext](func)