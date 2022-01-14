package com.louis.spark.project.domain

/**
 *
 * @param ip ip address
 * @param time log time
 * @param productId product id
 * @param statusCode log status
 * @param referer log reference
 */
case class ClickLog(ip: String, time: String, productId: Int, statusCode: Int, referer: String)