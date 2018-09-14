import java.util.Properties

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KeyValue

import scala.util.Random

/*
You can use kafka-console-consumer to consume produced messages:
 docker-compose exec kafka kafka-console-consumer.sh \
  --bootstrap-server kafka:9092 \
  --topic user_info \
  --property print.key=true \
  --property print.value=true \
  --property print.timestamp=true

You can use kafka-console-producer to manually produce message:
  docker-compose exec kafka kafka-console-producer.sh \
    --bootstrap-server kafka:9092 \
    --topic user_info \
    --property "parse.key=true" \
    --property "key.separator=:" \
    --from-beginning
*/
object SampleDataProducer {
  def main(args: Array[String]): Unit = {
    // initial Kafka producer
    val prop = new Properties()
    prop.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
    val producer = new KafkaProducer[String, String](prop, Serdes.String.serializer, Serdes.String.serializer)

    // produce user data into user_info topic
    Data.users.foreach { u =>
      val msg = new KeyValue(u.id, u.name)
      producer.send(new ProducerRecord[String, String]("user_info", msg.key, msg.value))
      println(s"send $msg to user_info topic")
    }
    println

    // produce location data into location_info topic
    Data.locations.foreach { c =>
      val msg = new KeyValue(c.name, c.x)
      producer.send(new ProducerRecord[String, String]("location_info", msg.key, msg.value))
      println(s"send $msg to location_info topic")
    }
    println

    // produce user location into user_location topic
    // users endless move around between A an F in different speed
    // it's an endless loop...
    val (usersLocation, usersSpeed) = Data.users.map { u =>
      val initLocation = "0"
      val initSpeed = randomSpeed()
      println(s"user [${u.id}] start endless moving from [$initLocation] with speed [$initSpeed]")
      (UserLocation(u.id, initLocation), (u.id, initSpeed))
    }.unzip
    endlessMoving(producer, usersLocation, usersSpeed.toMap)

    producer.flush()
    producer.close()
  }

  def randomSpeed(min: Int = 60, max: Int = 180): Int = min + new Random().nextInt((max - min) + 1)

  def endlessMoving(
                     producer: KafkaProducer[String, String],
                     uLocations: Seq[UserLocation],
                     uSpeeds: Map[String, Int]
                   ): Unit = {
    def move(uLocation: UserLocation, speed: Int): UserLocation = {
      UserLocation(uLocation.id, (uLocation.x.toDouble + speed / 3600.0 * 1000).toString)
    }

    // expose new location to user_location topic
    uploadUserLocation(producer, uLocations)
    // move to new location
    val newLocations = uLocations.map(u => move(u, uSpeeds(u.id)))
    // if touch end points, change speed
    val newSpeeds = newLocations.map { u =>
      val newSpeed = randomSpeed()
      if (u.x.toDouble <= 0.0) {
        println(s"user [${u.id}] touch endpoint [0.0], change speed to [$newSpeed]")
        (u.id, newSpeed)
      } else if (u.x.toDouble >= 500.0) {
        println(s"user [${u.id}] touch endpoint [500.0], change speed to [${-newSpeed}]")
        (u.id, -newSpeed)
      } else (u.id, uSpeeds(u.id))
    }.toMap
    Thread.sleep(10 * 1000)
    endlessMoving(producer, newLocations, newSpeeds)
  }

  def uploadUserLocation(producer: KafkaProducer[String, String], uLocations: Seq[UserLocation]): Unit = {
    uLocations.foreach { u =>
      val msg = new KeyValue(u.id, "%.2f".format(u.x.toDouble))
      producer.send(new ProducerRecord[String, String]("user_location", msg.key, msg.value))
      println(s"send $msg to user_location topic")
    }
  }
}
