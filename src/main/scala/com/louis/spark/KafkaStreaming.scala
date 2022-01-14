package com.louis.spark

import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

object KafkaStreaming {
  def main(args: Array[String]): Unit = {
    if (args.length != 4) {
      System.err.println("usage: zkQuorum, group, topics, numThreads")
      System.exit(1)
    }

    val Array(zkQuorum, group, topics, numThreads) = args
    val sparkconf = new SparkConf().setAppName("KafkaStreaming").setMaster("local[10]")
    val ssc = new StreamingContext(sparkconf, Seconds(5))

    //integrate kafka
    val topicsMap = topics.split((",")).map((_, numThreads.toInt)).toMap
    val message = KafkaUtils.createStream(ssc, zkQuorum, group, topicsMap)

    message.map(_._2).count().print()

    ssc.start()
    ssc.awaitTermination()
  }
}
