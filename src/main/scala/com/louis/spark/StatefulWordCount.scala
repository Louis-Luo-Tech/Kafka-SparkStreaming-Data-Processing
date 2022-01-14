package com.louis.spark

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

object StatefulWordCount {
  def main(args: Array[String]): Unit = {

    val sparkconf: SparkConf = new SparkConf().setMaster("local[10]").setAppName("StatefulWordCount") //local[?] ?>=2 is needed because there is a receiver that need thread
    val ssc = new StreamingContext(sparkconf, Seconds(1))
    //it is recommended that the checkpont should be a folder on HDFS
//if stateful operator is used, then checkpoint must be used
    ssc.checkpoint(".")

    val lines = ssc.socketTextStream("localhost", 6777)
    val words = lines.flatMap(_.split(" ")).map((_,1)) //(x => (x, 1))
    val state = words.updateStateByKey[Int](updateFunction _)
//    val windowedWordCounts = words.reduceByKeyAndWindow((_+_), Seconds(30), Seconds(10))
    state.print()
    ssc.start()
    ssc.awaitTermination()
  }
  /**
   * update the current data or old data
   * @param currentValues current data
   * @param preValues old data
   * @return
   */
  def updateFunction(currentValues: Seq[Int], preValues: Option[Int]): Option[Int] ={
    val current = currentValues.sum
    val pre = preValues.getOrElse(0)
    Some(current + pre)
    //Because Some and None are both children of Option
//    def toInt(in: String): Option[Int] = {
//      try {
//        Some(Integer.parseInt(in.trim))
//      } catch {
//        case e: NumberFormatException => None
//      }
//    }
  }


}
