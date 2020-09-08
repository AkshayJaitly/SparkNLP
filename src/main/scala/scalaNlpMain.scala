

/**
 * @created 8/30/20
 * @author akshayjaitly
 */

import com.johnsnowlabs.nlp.annotator._
import com.johnsnowlabs.nlp.base._
import com.johnsnowlabs.util.Benchmark
import org.apache.spark.ml.Pipeline
import utils.SparkUtil

object scalaNlpMain {
  def main(args: Array[String]): Unit = {

    val spark = SparkUtil.getSession
    val training_path = "src/labeled/labeled.txt"
    var df = spark.read.text(training_path)
    import org.apache.spark.sql.functions.{when, _}
    import spark.implicits._



    df = df.withColumn("_tmp", split($"value", "\\.")).select(
      $"_tmp".getItem(0).as("col1"),
      $"_tmp".getItem(1).as("col2")
    ).withColumn("col2",when(col("col2") === 0, "negative").otherwise("positive"))

    import scala.util.Random
    val data = 1 to 100 map(x =>  (1+Random.nextInt(100), 1+Random.nextInt(100), 1+Random.nextInt(100)))

    val sqlContext = spark.sqlContext
    sqlContext.createDataFrame(data).toDF("col1", "col2", "col3").show(false)

    df.show(false)

    import spark.implicits._
    val training = Seq(
      ("I really liked this movie!", "positive"),
      ("The cast was horrible", "negative"),
      ("Never going to watch this again or recommend it to anyone", "negative"),
      ("It's a waste of time", "negative"),
      ("I loved the protagonist", "positive"),
      ("The music was really really good", "positive"),
      ("The movie was ridiculous", "negative")
    ).toDF("train_text", "train_sentiment")


    val testing = Array(
      "I don't recommend this movie, it's horrible",
      "Dont waste your time!!!",
      "The movie was bad"
    )

    val count = testing.length

    val document = new DocumentAssembler()
      .setInputCol("train_text")
      .setOutputCol("document")

    val token = new Tokenizer()
      .setInputCols("document")
      .setOutputCol("token")

    val normalizer = new Normalizer()
      .setInputCols("token").setOutputCol("normal")

    val sentimentApproach = new ViveknSentimentApproach()
      .setInputCols("document", "normal")
      .setOutputCol("result_sentiment")
      .setSentimentCol("train_sentiment")

    val finisher = new Finisher()
      .setInputCols("result_sentiment")
      .setOutputCols("final_sentiment")

    val pipeline = new Pipeline().setStages(Array(document, token, normalizer, sentimentApproach, finisher))

    val sparkPipeline = pipeline.fit(training)

    val lightPipeline = new LightPipeline(sparkPipeline)

    Benchmark.time("Light pipeline quick annotation") {
      lightPipeline.annotate(testing)
    }

    Benchmark.time(f"Spark pipeline, this may be too much for just $count rows!") {
      val testingDS = testing.toSeq.toDS.toDF("testing_text")
      println("Updating DocumentAssembler input column")
      document.setInputCol("testing_text")
      sparkPipeline.transform(testingDS).show()
    }
  }
}
