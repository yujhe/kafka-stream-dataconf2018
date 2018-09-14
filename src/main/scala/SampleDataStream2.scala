import java.util.Properties
import java.util.concurrent.CountDownLatch

import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala.kstream.{KStream, KTable}
import org.apache.kafka.streams.scala.{Serdes, StreamsBuilder}
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig, Topology}

import scala.util.{Failure, Success, Try}

/*
  SampleDataStream2:
    This streaming application show you how to load Kafka topic as KStream and KTable,
    and how to join a KStream with a KTable
*/
object SampleDataStream2 {
  def main(args: Array[String]): Unit = {
    import Serdes._

    // Use the builders to define the actual processing topology, e.g. to specify
    // from which input topics to read, which stream operations (filter, map, etc.)
    // should be called, and so on.  We will cover this in detail in the subsequent
    // sections of this Developer Guide.
    val builder: StreamsBuilder = new StreamsBuilder()
    val userInfo: KTable[String, String] = builder.table[String, String]("user_info")
    val userLocation: KStream[String, String] = builder.stream[String, String]("user_location")
    // join user_location with user_info
    // you need to ensure joined topics is co-partitioning,
    // which means the both topics should have same partition number, and same partitioning strategy
    // https://kafka.apache.org/20/documentation/streams/developer-guide/dsl-api.html#join-co-partitioning-requirements
    userLocation
      .leftJoin(userInfo)((location, name) => (if (name == null) "unknown" else name, location))
      .map[String, String]((_, v) => (v._1, v._2))
      .foreach((name, location) => println(s"user [$name] current location [$location]"))

    val topology: Topology = builder.build()
    println(topology.describe())

    // Use the configuration to tell your application where the Kafka cluster is,
    // which Serializers/Deserializers to use by default, to specify security settings,
    // and so on.
    val props = new Properties()
    props.put(StreamsConfig.APPLICATION_ID_CONFIG, "dataconf-stream-2")
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
      latch.await()
    } match {
      case Success(_) =>
      case Failure(e) =>
        println("oooh... something wrong")
        System.exit(1)
    }
  }
}
