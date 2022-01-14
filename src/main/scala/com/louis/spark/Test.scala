package com.louis.spark

import java.sql.DriverManager

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.streaming.{Seconds, StreamingContext}

object Test {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[10]").setAppName("TransformApp")
    val sc = new SparkContext(sparkConf)

    val names = Seq("778,zs","779,ls","777,ww")
    //  (zs,(778,zs))
    val namesRDD = sc.parallelize(names).map(x=>(x.split(",")(1),x))
    val blacklist = Seq("zs","ls")
    //    (zs,true)
    val blacklistRDD = sc.parallelize(blacklist).map((_,true))

    val joinedRDD = namesRDD.leftOuterJoin(blacklistRDD).filter(x=>x._2._2.getOrElse(false) != true).map(x=>x._2._1)
//    (ww,(777,ww,None))
//    (zs,(778,zs,Some(true)))
//    (ls,(779,ls,Some(true)))
    joinedRDD.foreach(println)
//    val clicklog = lines.map(x=>(x.split(",")(1),x)).transform(rdd =>{
//      rdd.leftOuterJoin(blacklistRDD)
//        .filter(x=>x._2._2.getOrElse(false) != true)
//        .map(x=>x._2._1)
//    })
    sc.stop()
  }
}
