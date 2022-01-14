package com.louis.spark

import java.sql.DriverManager

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

object ForeachRDDApp {
  def main(args: Array[String]): Unit = {

    val sparkconf: SparkConf = new SparkConf().setMaster("local[10]").setAppName("ForeachRDDApp") //local[?] ?>=2 is needed because there is a receiver that need thread
    val ssc = new StreamingContext(sparkconf, Seconds(1))
    //it is recommended that the checkpont should be a folder on HDFS

//    ssc.checkpoint(".")

    val lines = ssc.socketTextStream("localhost", 6766)
    val result = lines.flatMap(_.split(" ")).map((_,1)).reduceByKey(_+_)

//    result.foreachRDD(rdd =>{
//      val connection = createConnection()
//      rdd.foreach{ record =>
//          val sql = "insert into wordcont(word,wordcount) values('"+record._1+"',"+record._2+")"
//          connection.createStatement().execute(sql)
//          //org.apache.spark.SparkException: Task not serializable
//      }
//    })

    result.print()

    result.foreachRDD(rdd =>{
      rdd.foreachPartition(partitionOfRecords =>{
          val connection = createConnection()
          partitionOfRecords.foreach(pair =>{
            val sql = "insert into wordcount(word,wordcount) values('"+ pair._1 +"',"+ pair._2 +")"
            connection.createStatement().execute(sql)
          })
          connection.close()
      })
    })



    ssc.start()
    ssc.awaitTermination()
  }

  /**
   * get the mysql connection
   */
  def createConnection() = {
    Class.forName("com.mysql.jdbc.Driver")
    DriverManager.getConnection("jdbc:mysql://localhost:3306/data?serverTimezone=PST","root","12345678")
  }

}
