package com.louis.spark

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

object TransformApp {
  def main(args: Array[String ]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[10]").setAppName("TransformApp")
    val ssc = new StreamingContext(sparkConf,Seconds(5))


    /**
     * create the blacklist
     */



    val blacklist = List("zs","ls")
    val blacklistRDD = ssc.sparkContext.parallelize(blacklist).map((_,true))
//    blacklistRDD.foreach(println)
    val lines = ssc.socketTextStream("localhost",6789)
//(zs,(778,zs))
    val clicklog = lines.map(x=>(x.split(",")(1),x)).transform(rdd =>{
      rdd.leftOuterJoin(blacklistRDD)
        .filter(x=>x._2._2.getOrElse(false) != true)
        .map(x=>x._2._1)
    })
    clicklog.print()
    ssc.start()
    ssc.awaitTermination()
  }
}
