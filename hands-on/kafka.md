# Hands-On Exercise: Using Kakfa As A Message Queue

On this lab, we use setup a kafka as 

## I. Follow [Kafka Quickstart](!https://kafka.apache.org/quickstart) instruction (until step #06)

### Step #01: Make kafka working directory
```
mkdir /tmp/kafka
cd /tmp/kafka
```

### Step #02: Download kafka binary

```
wget https://www-eu.apache.org/dist/kafka/2.3.0/kafka_2.12-2.3.0.tgz

```

### Step #03: Extract the binary

```
tar -vzxf kafka_2.12-2.3.0.tgz
cd kafka_2.12-2.3.0
```

### Step #04: Start the server

- Start zookeeper

```
bin/zookeeper-server-start.sh config/zookeeper.properties

[2019-11-07 22:27:56,899] INFO minSessionTimeout set to -1 (org.apache.zookeeper.server.ZooKeeperServer)
[2019-11-07 22:27:56,899] INFO maxSessionTimeout set to -1 (org.apache.zookeeper.server.ZooKeeperServer)
[2019-11-07 22:27:56,906] INFO Using org.apache.zookeeper.server.NIOServerCnxnFactory as server connection factory (org.apache.zookeeper.server.ServerCnxnFactory)
[2019-11-07 22:27:56,911] INFO binding to port 0.0.0.0/0.0.0.0:2181 (org.apache.zookeeper.server.NIOServerCnxnFactory)
```

- Start broker
```
bin/kafka-server-start.sh config/server.properties

[2019-11-07 22:29:06,792] INFO Kafka commitId: fc1aaa116b661c8a (org.apache.kafka.common.utils.AppInfoParser)
[2019-11-07 22:29:06,792] INFO Kafka startTimeMs: 1573140546785 (org.apache.kafka.common.utils.AppInfoParser)
[2019-11-07 22:29:06,794] INFO [KafkaServer id=0] started (kafka.server.KafkaServer)
```

### Step #05: Create a topic

- Let's create a topic named "test" with a single partition and only one replica:

```
bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic test
```

- We can now see that topic if we run the list topic command:
```
bin/kafka-topics.sh --list --bootstrap-server localhost:9092

test
```

### Step #06: Send some messages

Run the producer and then type a few messages into the console to send to the server.

```
bin/kafka-console-producer.sh --broker-list localhost:9092 --topic test

This is a message
This is another message
```

### Step #07: Start a consumer

Dump out messages to standard output
```
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test --from-beginning

This is a message
This is another message
```

### Step #08: Setting up a multi-broker cluster
- Make a config file for each of the brokers

```
cp config/server.properties config/server-1.properties
cp config/server.properties config/server-2.properties
```

- Now edit these new files and set the following properties
```
vi config/server-1.properties

broker.id=1
listeners=PLAINTEXT://:9093
log.dirs=/tmp/kafka-logs-1
zookeeper.connect=localhost:2181
```

```
vi config/server-2.properties

broker.id=2
listeners=PLAINTEXT://:9094
log.dirs=/tmp/kafka-logs-2
zookeeper.connect=localhost:2181
```

- Now create a new topic with a replication factor of three:

```
bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 3 --partitions 1 --topic my-replicated-topic
```

- Okay but now that we have a cluster how can we know which broker is doing what? To see that run the "describe topics" command:

```
bin/kafka-topics.sh --describe --bootstrap-server localhost:9092 --topic my-replicated-topic

Topic:my-replicated-topic	PartitionCount:1	ReplicationFactor:3	Configs:
	Topic: my-replicated-topic	Partition: 0	Leader: 2	Replicas: 2,1,0	Isr: 2,1,0
```

Here is an explanation of output. The first line gives a summary of all the partitions, each additional line gives information about one partition. Since we have only one partition for this topic there is only one line.

- "leader" is the node responsible for all reads and writes for the given partition. Each node will be the leader for a randomly selected portion of the partitions.
- "replicas" is the list of nodes that replicate the log for this partition regardless of whether they are the leader or even if they are currently alive.
- "isr" is the set of "in-sync" replicas. This is the subset of the replicas list that is currently alive and caught-up to the leader.

Note that in my example node 2 is the leader for the only partition of the topic

We can run the same command on the original topic we created to see where it is:

```
bin/kafka-topics.sh --describe --bootstrap-server localhost:9092 --topic test


Topic:test	PartitionCount:1	ReplicationFactor:1	Configs:
	Topic: test	Partition: 0	Leader: 0	Replicas: 0	Isr: 0
```

Let's publish a few messages to our new topic

```
bin/kafka-console-producer.sh --broker-list localhost:9092 --topic my-replicated-topic

my test message 1
my test message 2
```

Now let's consume these messages:

```
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --from-beginning --topic my-replicated-topic

my test message 1
my test message 2
```

Now let's test out fault-tolerance. Broker 1 was acting as the leader so let's kill it

```
ps aux | grep server-2.properties

vinhdp   19077  4.4  4.9 6821896 395664 pts/11 Sl....

kill -9 19077
```

Leadership has switched to one of the followers and node 1 is no longer in the in-sync replica set:

```
bin/kafka-topics.sh --describe --bootstrap-server localhost:9092 --topic my-replicated-topic


Topic:my-replicated-topic	PartitionCount:1	ReplicationFactor:3	Configs:segment.bytes=1073741824
	Topic: my-replicated-topic	Partition: 0	Leader: 1	Replicas: 2,1,0	Isr: 1,0
```

But the messages are still available for consumption even though the leader that took the writes originally is down:

```
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --from-beginning --topic my-replicated-topic


my test message 1
my test message 2
```

Congratulations!

## Exercise #01: Create a topic to receive log from previous logstash pipeline

- Create topic fplay.rawlogs.view

```
> bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 3 --partitions 1 --topic fplay.rawlogs.view
```

- Edit logstash configuration add kafka output plugins to forward messages to kafka

```
vi /tmp/config/http_collector.conf

input {
 http {
    host => "0.0.0.0"
    port => "9999"
  }
}

output {
  file {
     path => "/tmp/fptplay-event.log"
  }
  kafka {
     codec => json
     topic_id => "fplay.rawlogs.view"
     bootstrap_servers => "localhost:9092"
  }
}

```
- Send http request to logstash endpoint

```
curl "http://localhost:9999/i?p=web-playfpt&av=1.0.1&tna=fplay&tv=1.1.0&eid=03388ce9-4e35-442e-8de5-e99725a629d2&did=7a7da44c-0c1b-4739-997b-f745f553f554&tz=UTC%2B07:00&dtm=2019-11-06+23:09:36.799&res=1920x1080&aid=fptplay&ast=1573056554839&dmf=Chrome&dml=77.0386512&ost=Linux&dvt=pc&eac=view&eca=scr&ela=%2F"
```


- Check if kafka recieved message every time you send http request to collector endpoint

```
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --from-beginning --topic fplay.rawlogs.view


{"@version":"1","host":"127.0.0.1","@timestamp":"2019-11-07T16:08:00.704Z","headers":{"request_method":"GET","http_version":"HTTP/1.1","http_host":"localhost:9999","content_length":"0","http_accept":"*/*","request_path":"/i?p=web-playfpt&av=1.0.1&tna=fplay&tv=1.1.0&eid=03388ce9-4e35-442e-8de5-e99725a629d2&did=7a7da44c-0c1b-4739-997b-f745f553f554&tz=UTC%2B07:00&dtm=2019-11-06+23:09:36.799&res=1920x1080&aid=fptplay&ast=1573056554839&dmf=Chrome&dml=77.0386512&ost=Linux&dvt=pc&eac=view&eca=scr&ela=%2F","http_user_agent":"curl/7.58.0"},"message":""}

```

Tada!!!!!!! You are awesome!