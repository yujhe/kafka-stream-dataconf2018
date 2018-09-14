import java.util.Properties
import java.util.concurrent.CountDownLatch

import org.apache.kafka.streams.kstream.Materialized
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala.kstream.{KStream, KTable}
import org.apache.kafka.streams.scala.{ByteArrayKeyValueStore, _}
import org.apache.kafka.streams.state.QueryableStoreTypes
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig, Topology}

import scala.util.{Failure, Success, Try}

/*
  SampleDataStream1:
    This streaming application show you how to load Kafka topic as KStream and KTable,
    and dump state stored in local state store.
*/
object SampleDataStream1 {
  def main(args: Array[String]): Unit = {
    import Serdes._

    // Use the builders to define the actual processing topology, e.g. to specify
    // from which input topics to read, which stream operations (filter, map, etc.)
    // should be called, and so on.  We will cover this in detail in the subsequent
    // sections of this Developer Guide.
    val builder: StreamsBuilder = new StreamsBuilder()
    val userInfo: KTable[String, String] = builder.table[String, String](
      "user_info",
      Materialized.as[String, String, ByteArrayKeyValueStore]("user_info_store")
    )
    val userLocation: KStream[String, String] = builder.stream[String, String]("user_location")
    // dump record in userLocation stream
    userLocation.foreach((k, v) => println(s"user [$k] current location [$v]"))

    val topology: Topology = builder.build()
    println(topology.describe())

    // Use the configuration to tell your application where the Kafka cluster is,
    // which Serializers/Deserializers to use by default, to specify security settings,
    // and so on.
    val props = new Properties()
    props.put(StreamsConfig.APPLICATION_ID_CONFIG, "dataconf-stream-1")
    props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
    // specify default (de)serializers for record keys and for record values
    props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName)
    props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName)
    props.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, "1")

    val streams: KafkaStreams = new KafkaStreams(topology, props)

    val latch = new CountDownLatch(1)
    streams.setUncaughtExceptionHandler((t: Thread, e: Throwable) => {
      // here you should examine the throwable/exception and perform an appropriate action!
      println("oooh... something unexpected exception happened")
      latch.countDown()
    })
    // Add shutdown hook to respond to SIGTERM and gracefully close Kafka Streams
    Runtime.getRuntime.addShutdownHook(new Thread("streams-shutdown-hook") {
      override def run(): Unit = {
        println("shutdown application")
        streams.close()
        latch.countDown()
      }
    })

    Try {
      streams.start()
      Thread.sleep(5000) // we need some time to wait for state store initialization
      // Get the values for all of the keys available in this application instance
      val userInfoStore = streams.store(
        "user_info_store",
        QueryableStoreTypes.keyValueStore[String, String]()
      )
      val userInfoRange = userInfoStore.all
      while (userInfoRange.hasNext) {
        val uInfo = userInfoRange.next
        println(s"user info. id: ${uInfo.key}, name: ${uInfo.value}")
      }
      latch.await()
    } match {
      case Success(_) =>
      case Failure(e) =>
        println("oooh... something wrong")
        System.exit(1)
    }
  }
}
