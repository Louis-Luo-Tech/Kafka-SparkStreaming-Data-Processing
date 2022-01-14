package com.louis.spark.project.utils

import java.util.Date

import org.apache.commons.lang3.time.FastDateFormat

/**
 *
 * date utils
 */
object DateUtils {

  val inputFormat = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss")
  val outputFormat = FastDateFormat.getInstance("yyyyMMddHHmmss")

  def getTime(time: String) = {
    inputFormat.parse(time).getTime
  }

  def parseToMinute(time: String) = {
    outputFormat.format(new Date(getTime(time)))
  }

  def main(args: Array[String]): Unit = {
    println(parseToMinute("2020-05-30 15:37:59"))
//    println(inputFormat.parse("2020-05-30 15:37:59").getTime)
  }
}
