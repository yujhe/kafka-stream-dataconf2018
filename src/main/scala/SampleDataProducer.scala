import java.util.Properties

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KeyValue

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

    // produce city data into city_info topic
    Data.cities.foreach { c =>
      val msg = new KeyValue(c.name, c.x)
      producer.send(new ProducerRecord[String, String]("city_info", msg.key, msg.value))
      println(s"send $msg to city_info topic")
    }
    println

    // produce user location into user_location topic
    // users move from Taipei to Kaohsiung in different speed and turn back to Taipei
    // and move to Kaohsiung again... it's an endless loop

    producer.flush()
    producer.close()
  }
}
