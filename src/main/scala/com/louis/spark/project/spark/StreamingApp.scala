package com.louis.spark.project.spark

import com.louis.spark.project.dao.{ProductClickCountDAO, ProductSearchClickCountDAO}
import com.louis.spark.project.domain.{ClickLog, ProductClickCount, ProductSearchClickCount}
import com.louis.spark.project.utils.DateUtils
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

import scala.collection.mutable.ListBuffer

object StreamingApp {
  def main(args: Array[String]): Unit = {
    if (args.length != 4) {
      System.err.println("usage: zkQuorum, group, topics, numThreads")
      System.exit(1)
    }
    val Array(zkQuorum, group, topics, numThreads) = args

    val sparkConf: SparkConf = new SparkConf().setAppName("StreamingApp").setMaster("local[*]")
    val ssc = new StreamingContext(sparkConf, Seconds(10))

    val topicsMap = topics.split((",")).map((_, numThreads.toInt)).toMap

    val message = KafkaUtils.createStream(ssc, zkQuorum, group, topicsMap)

    //    message.map(_._2).count().print()
    //data cleaning

    val logs = message.map(_._2)

    val cleanData = logs.map(line => {
      //      43.29.63.167	2020-05-30 15:50:25	"GET /product/14610854.html HTTP/1.1"	500	https://www.bing.com/search?q=ps4
      val infos = line.split("\t")
      // "GET /product/14610854.html HTTP/1.1"
      val url = infos(2).split(" ")(1)
      var productId = 0
      //  /product/14610854.html
      if (url.startsWith("/product")) {
        val productIdHTML = url.split("/")(2) // 14610854.html
        productId = productIdHTML.substring(0, productIdHTML.lastIndexOf(".")).toInt
      }
      ClickLog(infos(0), DateUtils.parseToMinute(infos(1)), productId, infos(3).toInt, infos(4))
    }).filter(ClickLog => ClickLog.productId != 0)

    //    cleanData.print()
    //function one
    cleanData.map(x => {
      //rowkey
      (x.time.substring(0, 8) + "_" + x.productId, 1)
    }).reduceByKey(_ + _).foreachRDD(rdd => {
      rdd.foreachPartition(partitionRecords => {
        val list = new ListBuffer[ProductClickCount]
        partitionRecords.foreach(pair => {
          list.append(ProductClickCount(pair._1, pair._2))
        })
        ProductClickCountDAO.save(list)
      })
    })

    //function two

    cleanData.map(x=>{
//      https://www.google.com/search?sxsrf=ps4
      val referer = x.referer.replaceAll("//","/");
      val splits = referer.split("/")
      var host = ""
      if(splits.length > 2){
        host = splits(1)
      }
      (host,x.productId,x.time)
    }).filter(_._1 != "").map(x =>{
      (x._3.substring(0,8) + "_" + x._1 + x._2,1)
    }).reduceByKey(_ + _).foreachRDD(rdd =>{
      rdd.foreachPartition(partitionRecords =>{
        val list = new ListBuffer[ProductSearchClickCount]
        partitionRecords.foreach(pair =>{
          list.append(ProductSearchClickCount(pair._1,pair._2))
        })
        ProductSearchClickCountDAO.save(list)
      })
    })

    ssc.start()
    ssc.awaitTermination()
  }
}
