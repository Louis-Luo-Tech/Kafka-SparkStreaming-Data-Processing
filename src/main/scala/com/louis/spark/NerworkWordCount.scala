package com.louis.spark

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

object NerworkWordCount {
  def main(args: Array[String]): Unit = {
    //    StreamingExamples.setStreamingLogLevels()

    val sparkconf: SparkConf = new SparkConf().setMaster("local[10]").setAppName("NerworkWordCount") //local[?] ?>=2 is needed because there is a receiver that need thread
    val ssc = new StreamingContext(sparkconf, Seconds(5))
    val lines = ssc.socketTextStream("localhost", 8222)
    val words = lines.flatMap(_.split(" "))
    val wordscount = words.map(x => (x, 1)).reduceByKey(_ + _)
    wordscount.print()
    ssc.start()
    ssc.awaitTermination()
  }
}