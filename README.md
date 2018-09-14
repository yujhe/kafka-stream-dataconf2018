# kafka-stream-dataconf2018
Tutorial for DataConf.TW 2018

## Pre-Requisites
* Install docker-compose https://docs.docker.com/compose/install/
* java 1.8
* sbt 0.13.7
* scala 2.12.6

## Environment Setup
* Start Zookeeper and Kafka docker container
```bash
# docker-env/
docker-compose up -d
```
* Check out container status
```bash
# docker-env/
$ docker-compose ps
         Name                       Command               State              Ports            
----------------------------------------------------------------------------------------------
docker-env_kafka_1       start-kafka.sh                   Up      0.0.0.0:9092->9092/tcp      
docker-env_zookeeper_1   /docker-entrypoint.sh zkSe ...   Up      2181/tcp, 2888/tcp, 3888/tcp

$ docker-compose exec zookeeper zkServer.sh status
ZooKeeper JMX enabled by default
Using config: /conf/zoo.cfg
Mode: standalone

$ docker-compose exec kafka kafka-topics.sh --zookeeper zookeeper --list
user_info
user_location
```

## Run Sample Streaming Application

### Sample Data
You can execute SampleDataProducer to produce sample data into these two sample topics: 
* user_info: (id: String, name: String)
* user_location: (id: String, location: String)

Or, you can use kafka-console-producer to manually produce message:
```bash
docker-compose exec kafka kafka-console-producer.sh \
  --broker-list kafka:9092 \
  --topic user_info \
  --property "parse.key=true" \
  --property "key.separator=:"
> 4:Webber
```

### Sample Streaming Application
* SampleDataStream1: This streaming application show you how to load Kafka topic as KStream and KTable, and dump state stored in local state store.
* SampleDataStream2: This streaming application show you how to load Kafka topic as KStream and KTable, and how to join a KStream with a KTable.

You can run those applications in your favorite IDE, or using command line run with assembly jar.

```bash
sbt assembly
java \
  -classpath target/scala-2.12/kafka-stream-dataconf-assembly-0.0.1.jar \
  SampleDataStream1
```
