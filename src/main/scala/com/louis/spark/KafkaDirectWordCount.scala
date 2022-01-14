package com.louis.spark


import kafka.serializer.StringDecoder
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

object KafkaDirectWordCount {
  def main(args: Array[String]): Unit = {
    if (args.length != 2) {
      System.err.println("usage: brokers,topics")
      System.exit(1)
    }

    val Array(brokers,topics) = args
    val sparkconf = new SparkConf().setAppName("KafkaDirectWordCount").setMaster("local[10]")
    val ssc = new StreamingContext(sparkconf, Seconds(5))

    //integrate kafka
    val topicSet = topics.split(",").toSet

    val kafkaParams = Map[String, String]("metadata.broker.list" -> brokers)

    val message = KafkaUtils.createDirectStream[String,String, StringDecoder,StringDecoder](ssc,kafkaParams,topicSet)

    message.map(_._2).flatMap(_.split(" ")).map((_,1)).reduceByKey(_+_).print()

    ssc.start()
    ssc.awaitTermination()
  }
}
