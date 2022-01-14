package com.louis.spark.project.domain

/**
 *
 * @param day_product rowkey 20200505_www.google.com_1
 * @param click_count total count
 */
case class ProductSearchClickCount(day_search_product:String, click_count: Long)