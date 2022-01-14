package com.louis.spark.project.domain

/**
 *
 * @param day_product rowkey 20200505_1
 * @param click_count total count
 */
case class ProductClickCount(day_product:String, click_count: Long)