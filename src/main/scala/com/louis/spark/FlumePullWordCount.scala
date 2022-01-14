package com.louis.spark

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.flume.FlumeUtils

object FlumePullWordCount {
  def main(args: Array[String]): Unit = {
    if(args.length != 2){
      System.err.println("Usage: FlumePushWordCount <hostname> <port>")
      System.exit(1)
    }

    val Array(hostname,port) = args


    val sparkconf = new SparkConf().setMaster("local[10]").setAppName("FlumePushWordCount")
    val ssc = new StreamingContext(sparkconf,Seconds(5))

    val flumeStream = FlumeUtils.createPollingStream(ssc,hostname,port.toInt)

    flumeStream.map(x=> new String(x.event.getBody.array()).trim)
      .flatMap(_.split(" ")).map((_,1)).reduceByKey(_+_)
      .print()

    ssc.start()
    ssc.awaitTermination()
  }
}
