# kafka-stream-dataconf2018
Tutorial for DataConf.TW 2018

## Pre-Requisites
* Install docker-compose https://docs.docker.com/compose/install/

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
city_info
user_info
user_location
```
