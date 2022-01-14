package com.louis.spark.project.dao

import com.louis.spark.project.domain.ProductSearchClickCount
import com.louis.spark.project.utils.HBaseUtils
import org.apache.hadoop.hbase.client.Get
import org.apache.hadoop.hbase.util.Bytes

import scala.collection.mutable.ListBuffer

object ProductSearchClickCountDAO {

  val tablName = "product_search_clickcount"
  val cf = "info"
  val qualifer = "click_count"

  /**
   * save the data to hbase
   *
   * @param list the collection of ProductClickCount
   */
  def save(list: ListBuffer[ProductSearchClickCount]): Unit = {

    val table = HBaseUtils.getInstance().getTableName(tablName)

    for(ele <- list){
      table.incrementColumnValue(Bytes.toBytes(ele.day_search_product),
        Bytes.toBytes(cf),
        Bytes.toBytes(qualifer),
        ele.click_count)
    }
  }

  /**
   * select the count value according to rowkey
   * @param day_count
   * @return
   */
  def count(day_search_product: String): Long = {

    val table = HBaseUtils.getInstance().getTableName(tablName)
    val get = new Get(Bytes.toBytes(day_search_product))
    val value = table.get(get).getValue(cf.getBytes(), qualifer.getBytes())

    if (value == null) {
      0L
    } else {
      Bytes.toLong(value)
    }

  }

  def main(args: Array[String]): Unit = {
    val list = new ListBuffer[ProductSearchClickCount]
    list.append(ProductSearchClickCount("20191111_www.google.com_1",10))
    list.append(ProductSearchClickCount("20191111_www.bing.com.2",20))

    save(list)

    println(count("20191111_www.google.com_1") + ":" + count("20191111_www.bing.com.2"));
  }
}
