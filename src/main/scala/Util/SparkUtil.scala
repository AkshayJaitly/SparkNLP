package Util

import org.apache.spark.sql.SparkSession

/**
 * @created 8/30/20
 * @author akshayjaitly
 */
object SparkUtil {
  def getSession: SparkSession = {
    val spark = SparkSession
      .builder()
      .appName("nlp")
      .master("local[*]")
      .config("spark.driver.memory", "12G")
      .config("spark.driver.host", "127.0.0.1") // needed for local
      .config("spark.driver.bindAddress", "127.0.0.1") // needed for local
      .config("spark.kryoserializer.buffer.max", "200M")
      .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .getOrCreate()
    val hadoopConfig = spark.sparkContext.hadoopConfiguration
    hadoopConfig.set("fs.file.impl", classOf[org.apache.hadoop.fs.LocalFileSystem].getName)
    spark.sparkContext.setLogLevel("WARN")
    spark
  }

}
