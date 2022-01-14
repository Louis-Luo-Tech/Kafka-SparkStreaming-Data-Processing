package com.louis.spark

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

object FileWordCount {
  def main(args: Array[String]): Unit = {
    val sparkconf: SparkConf = new SparkConf().setMaster("local").setAppName("FileWordCount") //local[1] is fine
    val ssc = new StreamingContext(sparkconf, Seconds(5))
    val lines = ssc.textFileStream("/Users/xiangluo/data/sparkstreaming")
    val words = lines.flatMap(_.split(" "))
    val wordscount = words.map(x => (x, 1)).reduceByKey(_ + _)
    wordscount.print()
    ssc.start()
    ssc.awaitTermination()

  }

}
