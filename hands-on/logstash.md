# Hands-On Exercise: Using Logstash - Centralize, transform & stash your data

This lab provide:

- A simple solution to build a http collector endpoint using logstash
- Some basic logstash configuration using for log centralize, transformation and push the data to other component for further processing

## Step #01: Download and install Logstash

- Make logstash directory
```
mkdir /tmp/logstash
cd /tmp/logstash
```
- Download logstash binary package

```
wget https://artifacts.elastic.co/downloads/logstash/logstash-7.4.2.tar.gz
```

- Extract the binary
```
tar -vzxf logstash-7.4.2.tar.gz
```

- Change directory
```
cd /tmp/logstash/logstash-7.4.2
```

- Check logstash version
```
./bin/logstash -V
```

Output
```
logstash 7.4.2
```

## Step #02: Write simple pipeline using Logstash

### Problems
Your project need to tracking user behaviour when they are using FPT Play. The user activity include the following attributes after you gather information need to track:

```
p: platform
av: app version
eid: event id
did: device id
res: screen resolution
dtm: client timestamp
eac: event action (event type)
```

You need to design a tracking endpoint to collect above information like this:

```
http://118.68.170.148:9999/i?p=web-playfpt&av=1.0.1&tna=fplay&tv=1.1.0&eid=03388ce9-4e35-442e-8de5-e99725a629d2&did=7a7da44c-0c1b-4739-997b-f745f553f554&tz=UTC%2B07:00&dtm=2019-11-06+23:09:36.799&res=1920x1080&aid=fptplay&ast=1573056554839&dmf=Chrome&dml=77.0386512&ost=Linux&dvt=pc&eac=view&eca=scr&ela=%2F
```

Requirement:

> Your task is setup an collection endpoint recieving http request from client app: web, mobile app,...

## Demo

#### 1. Create logstash configuration file

```
mkdir /tmp/config
vi /tmp/config/http_collector.conf
```

File content

```
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
}
```

#### 2. Test if it is working properly

```
./bin/logstash -t -f /tmp/config/http_collector.conf
```

The output should be

```
Sending Logstash logs to /tmp/logstash/logstash-7.4.2/logs which is now configured via log4j2.properties
[2019-11-06T23:20:41,684][WARN ][logstash.config.source.multilocal] Ignoring the 'pipelines.yml' file because modules or command line options are specified
[2019-11-06T23:20:43,102][INFO ][org.reflections.Reflections] Reflections took 75 ms to scan 1 urls, producing 20 keys and 40 values 
Configuration OK
[2019-11-06T23:20:43,669][INFO ][logstash.runner          ] Using config.test_and_exit mode. Config Validation Result: OK. Exiting Logstash

```
#### 3. Run the pipeline

```
./bin/logstash -f /tmp/config/http_collector.conf
```

The output should be

```
Sending Logstash logs to /tmp/logstash/logstash-7.4.2/logs which is now configured via log4j2.properties
[2019-11-06T23:23:40,560][WARN ][logstash.config.source.multilocal] Ignoring the 'pipelines.yml' file because modules or command line options are specified
[2019-11-06T23:23:40,576][INFO ][logstash.runner          ] Starting Logstash {"logstash.version"=>"7.4.2"}
[2019-11-06T23:23:40,608][INFO ][logstash.agent           ] No persistent UUID file found. Generating new UUID {:uuid=>"2682fc78-b005-483e-ad37-159f2b578b63", :path=>"/tmp/logstash/logstash-7.4.2/data/uuid"}
[2019-11-06T23:23:42,658][INFO ][org.reflections.Reflections] Reflections took 53 ms to scan 1 urls, producing 20 keys and 40 values 
[2019-11-06T23:23:43,398][WARN ][org.logstash.instrument.metrics.gauge.LazyDelegatingGauge][main] A gauge metric of an unknown type (org.jruby.RubyArray) has been create for key: cluster_uuids. This may result in invalid serialization.  It is recommended to log an issue to the responsible developer/development team.
[2019-11-06T23:23:43,432][INFO ][logstash.javapipeline    ][main] Starting pipeline {:pipeline_id=>"main", "pipeline.workers"=>8, "pipeline.batch.size"=>125, "pipeline.batch.delay"=>50, "pipeline.max_inflight"=>1000, :thread=>"#<Thread:0x724c1f8b run>"}
[2019-11-06T23:23:43,841][INFO ][logstash.javapipeline    ][main] Pipeline started {"pipeline.id"=>"main"}
[2019-11-06T23:23:43,965][INFO ][logstash.inputs.http     ][main] Starting http input listener {:address=>"0.0.0.0:9999", :ssl=>"false"}
[2019-11-06T23:23:44,071][INFO ][logstash.agent           ] Pipelines running {:count=>1, :running_pipelines=>[:main], :non_running_pipelines=>[]}
[2019-11-06T23:23:44,395][INFO ][logstash.agent           ] Successfully started Logstash API endpoint {:port=>9600}
```

#### 4. Let's send the first event message into this enpoint

```
curl "http://118.68.170.148:9999/i?p=web-playfpt&av=1.0.1&tna=fplay&tv=1.1.0&eid=03388ce9-4e35-442e-8de5-e99725a629d2&did=7a7da44c-0c1b-4739-997b-f745f553f554&tz=UTC%2B07:00&dtm=2019-11-06+23:09:36.799&res=1920x1080&aid=fptplay&ast=1573056554839&dmf=Chrome&dml=77.0386512&ost=Linux&dvt=pc&eac=view&eca=scr&ela=%2F"
```

The command output should be

```
ok
```

And the pipeline output

```
[2019-11-06T23:30:00,129][INFO ][logstash.outputs.file    ][main] Opening file {:path=>"/tmp/fptplay-event.log"}
[2019-11-06T23:30:13,797][INFO ][logstash.outputs.file    ][main] Closing file /tmp/fptplay-event.log
```

Now cat the output file to see if there is a new row

```
cat /tmp/fptplay-event.log

{"@version":"1","@timestamp":"2019-11-06T16:37:31.188Z","host":"100.96.24.199","message":"","headers":{"http_version":"HTTP/1.1","http_accept":"*/*","http_host":"118.68.170.148:9999","content_length":"0","request_path":"/i?p=web-playfpt&av=1.0.1&tna=fplay&tv=1.1.0&eid=03388ce9-4e35-442e-8de5-e99725a629d2&did=7a7da44c-0c1b-4739-997b-f745f553f554&tz=UTC%2B07:00&dtm=2019-11-06+23:09:36.799&res=1920x1080&aid=fptplay&ast=1573056554839&dmf=Chrome&dml=77.0386512&ost=Linux&dvt=pc&eac=view&eca=scr&ela=%2F","http_user_agent":"curl/7.58.0","request_method":"GET"}}
```

Congratulations! Your first simple logstash pipeline is working!

## Exercise #01 (15 mins):

There are many event type (eac) but right now we only care about the view action, and may be there are many events without eac as well as the wrong request come from users. You need to capture only `view` event.

> Hint: Using filter

## Exercise #02 (15 mins):

Currently, we capture all the events and write to only one file. This file become larger over time. We need to decrease the file size.

> Hint: Using filter, output plugin & compression


## Exercise #03 (15 mins):

Write log into file is not sufficient for us to monitor the number of view on our website. We need to process these events in realtime. You need to send events into Kafka.

> Hint: Using [Kafka Output Plugin](!https://www.elastic.co/guide/en/logstash/current/plugins-outputs-kafka.html)