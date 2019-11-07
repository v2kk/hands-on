# Hands-On Exercise: Load data from RDBMS to HDFS

This lab provide a quick startup for whom want to replicate data between RDBMS and Hadoop

---

We prepared a PostgreSQL database with the following connection information

```
Host: 118.68.168.182
Port: 5433
Database: fpt
Username: dev
Password: $PASSWORD
```

Check if you are able to connect to the database

```
psql -h 118.68.168.182 -p 5433 -d fpt -U dev -W
```

In the database fpt, there are one table with the following schema:

```
create table users (
    _Id integer primary key,
    _CreationDate timestamp,
    _DisplayName text,
    _LastAccessDate timestamp,
    _Reputation integer,
    _UpVotes integer,
    _Views integer
);
```

Load data into table (ignore this step)

```
psql -h 118.68.168.182 -p 5433 -d fpt -U dev -W -c "\COPY users FROM '~/Documents/demo/stackoverflow_users.csv' DELIMITERS ',' CSV HEADER;"
```

Show some records from table

```
fpt=> select * from users limit 10;
  _id  |      _creationdate      | _displayname  |     _lastaccessdate     | _reputation | _upvotes | _views 
-------+-------------------------+---------------+-------------------------+-------------+----------+--------
   915 | 2008-08-10 11:21:38.537 | Jules         | 2019-01-26 19:05:27.067 |           1 |        0 |    298
  2429 | 2008-08-22 08:48:22.173 | Seb Nilsson   | 2019-08-30 09:43:30.543 |       14387 |      417 |   1124
  4292 | 2008-09-02 17:49:20.163 | Kevin Kershaw | 2019-03-24 02:49:20.487 |         196 |       17 |    105
  6537 | 2008-09-15 12:11:43.593 | FooLman       | 2016-05-03 12:41:21.573 |         392 |       68 |     45
  6851 | 2008-09-15 12:43:17.807 | Fu86          | 2019-08-31 14:58:03.347 |        1747 |      171 |    243
  7736 | 2008-09-15 14:19:14.253 | William Jens  | 2019-08-30 15:21:39.957 |         166 |       14 |     89
 10405 | 2008-09-15 22:56:19.737 | jkinter       | 2017-10-29 00:00:41.597 |          68 |       11 |     21
 11176 | 2008-09-16 05:53:02.763 | afarnham      | 2019-08-31 01:01:12.84  |        2155 |       90 |    134
 13623 | 2008-09-16 20:14:59.553 | Erik          | 2009-01-03 03:12:02.53  |          76 |        1 |     21
 13780 | 2008-09-16 20:50:27.987 | Will Prescott | 2019-08-28 11:55:56.297 |        3665 |       78 |    102
(10 rows)

```


## Solution #01: Using RDBMS command COPY or write your own code to dump the table

### Step #01: Dump table users into csv
```
psql -h 118.68.168.182 -p 5433 -d fpt -U dev -W -c "\COPY users to '/tmp/users.csv' DELIMITERS ',' CSV HEADER;"
``` 

Output should be:

```
COPY 11084
```

You can check the dump version at `/tmp/users.csv`
```
head /tmp/users.csv

_id,_creationdate,_displayname,_lastaccessdate,_reputation,_upvotes,_views
915,2008-08-10 11:21:38.537,Jules,2019-01-26 19:05:27.067,1,0,298
2429,2008-08-22 08:48:22.173,Seb Nilsson,2019-08-30 09:43:30.543,14387,417,1124
4292,2008-09-02 17:49:20.163,Kevin Kershaw,2019-03-24 02:49:20.487,196,17,105
6537,2008-09-15 12:11:43.593,FooLman,2016-05-03 12:41:21.573,392,68,45
6851,2008-09-15 12:43:17.807,Fu86,2019-08-31 14:58:03.347,1747,171,243
7736,2008-09-15 14:19:14.253,William Jens,2019-08-30 15:21:39.957,166,14,89
10405,2008-09-15 22:56:19.737,jkinter,2017-10-29 00:00:41.597,68,11,21
11176,2008-09-16 05:53:02.763,afarnham,2019-08-31 01:01:12.84,2155,90,134
13623,2008-09-16 20:14:59.553,Erik,2009-01-03 03:12:02.53,76,1,21
```

Finaly, put this file into HDFS location as you want

```
hdfs dfs -put /tmp/users.csv /data/
```

Congratulations! You are done!

But this is only appropriate for small & medium data. If you have a lot of data (terabytes), it's not the best choice for big data!

---

## Solution #02: Using Sqoop, an efficiently transferring bulk data between Apache Hadoop and structured datastores such as relational

## Step #01: Setup Apache Sqoop

For detail guidelien, please following the [official documentation](!https://sqoop.apache.org/docs/1.99.1/Installation.html)

- Create working directory
```
mkdir /tmp/sqoop
cd /tmp/sqoop
```
- Download Apache Sqoop
```
wget https://www-eu.apache.org/dist/sqoop/1.4.7/sqoop-1.4.7.bin__hadoop-2.6.0.tar.gz
```

- Extract the binary
```
tar -vzxf sqoop-1.4.7.bin__hadoop-2.6.0.tar.gz
mv sqoop-1.4.7.bin__hadoop-2.6.0 sqoop-1.4.7
```

- Move to Sqoop home directory
```
cd /tmp/sqoop/sqoop-1.4.7
```

- Export Hadoop Common Envs
```
export SQOOP_HOME=/tmp/sqoop/sqoop-1.4.7
export HADOOP_HOME=/usr/hdp/current/hadoop-client
export HADOOP_COMMON_HOME=/usr/hdp/current/hadoop-client
export HADOOP_MAPRED_HOME=/usr/hdp/current/hadoop-client
```

- Test if Sqoop is working as expected
```
./bin/sqoop version
```

Output should be
```
...
19/11/06 21:25:00 INFO sqoop.Sqoop: Running Sqoop version: 1.4.7
Sqoop 1.4.7
git commit id 2328971411f57f0cb683dfb79d19d4d19d185dd8
Compiled by maugli on Thu Dec 21 15:59:58 STD 20
```

### Notes: Before running Sqoop job on PostgreSQL, you need to configuration PostgreSQL JDBC Driver in Sqoop lib folder

Download PostgreSQL JDBC Driver 

```
wget https://jdbc.postgresql.org/download/postgresql-42.2.8.jar
```

Copy the jar into Sqoop lib folder
```
cp postgresql-42.2.8.jar /tmp/sqoop/sqoop-1.4.7/lib/
```

## Step #02: Using Sqoop loading users table into HDFS

> Read [Sqoop User Guide](!https://sqoop.apache.org/docs/1.4.7/SqoopUserGuide.html) for other features & detail explaination

```
./bin/sqoop import --connect jdbc:postgresql://118.68.168.182:5433/fpt --table users --target-dir /tmp/users --username dev --password $PASSWORD
```

Output should be like this

```
19/11/06 21:36:17 INFO sqoop.Sqoop: Running Sqoop version: 1.4.7
19/11/06 21:36:17 WARN tool.BaseSqoopTool: Setting your password on the command-line is insecure. Consider using -P instead.
19/11/06 21:36:17 INFO manager.SqlManager: Using default fetchSize of 1000
19/11/06 21:36:17 INFO tool.CodeGenTool: Beginning code generation
19/11/06 21:36:17 INFO manager.SqlManager: Executing SQL statement: SELECT t.* FROM "users" AS t LIMIT 1
19/11/06 21:36:18 INFO orm.CompilationManager: HADOOP_MAPRED_HOME is /usr/hdp/3.0.1.0-187/hadoop-mapreduce
Note: /tmp/sqoop-hdfs/compile/77c2bd0b4f3d4b2411a5ac908b04e2bd/users.java uses or overrides a deprecated API.
Note: Recompile with -Xlint:deprecation for details.
19/11/06 21:36:19 INFO orm.CompilationManager: Writing jar file: /tmp/sqoop-hdfs/compile/77c2bd0b4f3d4b2411a5ac908b04e2bd/users.jar
19/11/06 21:36:19 WARN manager.PostgresqlManager: It looks like you are importing from postgresql.
19/11/06 21:36:19 WARN manager.PostgresqlManager: This transfer can be faster! Use the --direct
19/11/06 21:36:19 WARN manager.PostgresqlManager: option to exercise a postgresql-specific fast path.
19/11/06 21:36:19 INFO mapreduce.ImportJobBase: Beginning import of users
19/11/06 21:36:20 INFO client.RMProxy: Connecting to ResourceManager at vm-01.cse87.higio.net/100.64.1.1:8050
19/11/06 21:36:20 INFO client.AHSProxy: Connecting to Application History server at vm-02.cse87.higio.net/100.64.1.2:10200
19/11/06 21:36:21 INFO mapreduce.JobResourceUploader: Disabling Erasure Coding for path: /user/hdfs/.staging/job_1572888198619_0002
19/11/06 21:36:22 INFO db.DBInputFormat: Using read commited transaction isolation
19/11/06 21:36:22 INFO db.DataDrivenDBInputFormat: BoundingValsQuery: SELECT MIN("_id"), MAX("_id") FROM "users"
19/11/06 21:36:22 INFO db.IntegerSplitter: Split size: 3000999; Num splits: 4 from: 915 to: 12004912
19/11/06 21:36:22 INFO mapreduce.JobSubmitter: number of splits:4
19/11/06 21:36:22 INFO mapreduce.JobSubmitter: Submitting tokens for job: job_1572888198619_0002
19/11/06 21:36:22 INFO mapreduce.JobSubmitter: Executing with tokens: []
19/11/06 21:36:22 INFO conf.Configuration: found resource resource-types.xml at file:/etc/hadoop/3.0.1.0-187/0/resource-types.xml
19/11/06 21:36:22 INFO impl.YarnClientImpl: Submitted application application_1572888198619_0002
19/11/06 21:36:22 INFO mapreduce.Job: The url to track the job: http://VM-01.cse87.higio.net:8088/proxy/application_1572888198619_0002/
19/11/06 21:36:22 INFO mapreduce.Job: Running job: job_1572888198619_0002
19/11/06 21:36:33 INFO mapreduce.Job: Job job_1572888198619_0002 running in uber mode : false
19/11/06 21:36:33 INFO mapreduce.Job:  map 0% reduce 0%
19/11/06 21:36:38 INFO mapreduce.Job:  map 25% reduce 0%
19/11/06 21:36:43 INFO mapreduce.Job:  map 50% reduce 0%
19/11/06 21:36:44 INFO mapreduce.Job:  map 100% reduce 0%
19/11/06 21:36:44 INFO mapreduce.Job: Job job_1572888198619_0002 completed successfully
19/11/06 21:36:44 INFO mapreduce.Job: Counters: 32
	File System Counters
		FILE: Number of bytes read=0
		FILE: Number of bytes written=963604
		FILE: Number of read operations=0
		FILE: Number of large read operations=0
		FILE: Number of write operations=0
		HDFS: Number of bytes read=446
		HDFS: Number of bytes written=812855
		HDFS: Number of read operations=24
		HDFS: Number of large read operations=0
		HDFS: Number of write operations=8
	Job Counters 
		Launched map tasks=4
		Other local map tasks=4
		Total time spent by all maps in occupied slots (ms)=78627
		Total time spent by all reduces in occupied slots (ms)=0
		Total time spent by all map tasks (ms)=26209
		Total vcore-milliseconds taken by all map tasks=26209
		Total megabyte-milliseconds taken by all map tasks=80514048
	Map-Reduce Framework
		Map input records=11084
		Map output records=11084
		Input split bytes=446
		Spilled Records=0
		Failed Shuffles=0
		Merged Map outputs=0
		GC time elapsed (ms)=305
		CPU time spent (ms)=6480
		Physical memory (bytes) snapshot=901537792
		Virtual memory (bytes) snapshot=18423611392
		Total committed heap usage (bytes)=778567680
		Peak Map Physical memory (bytes)=227115008
		Peak Map Virtual memory (bytes)=4606341120
	File Input Format Counters 
		Bytes Read=0
	File Output Format Counters 
		Bytes Written=812855
19/11/06 21:36:44 INFO mapreduce.ImportJobBase: Transferred 793.8037 KB in 24.0058 seconds (33.0671 KB/sec)
19/11/06 21:36:44 INFO mapreduce.ImportJobBase: Retrieved 11084 records.
```

Let's check the data on HDFS

```
hdfs dfs -ls /tmp/users/
```

Output:
```
Found 5 items
-rw-r--r--   3 hdfs hdfs          0 2019-11-06 21:36 /tmp/users/_SUCCESS
-rw-r--r--   3 hdfs hdfs     177951 2019-11-06 21:36 /tmp/users/part-m-00000
-rw-r--r--   3 hdfs hdfs     210424 2019-11-06 21:36 /tmp/users/part-m-00001
-rw-r--r--   3 hdfs hdfs     204991 2019-11-06 21:36 /tmp/users/part-m-00002
-rw-r--r--   3 hdfs hdfs     219489 2019-11-06 21:36 /tmp/users/part-m-00003

```

Cat the data in HDFS

```
hdfs dfs -cat /tmp/users/part-m-00000 | head
```

Output:

```
915,2008-08-10 11:21:38.537,Jules,2019-01-26 19:05:27.067,1,0,298
2429,2008-08-22 08:48:22.173,Seb Nilsson,2019-08-30 09:43:30.543,14387,417,1124
4292,2008-09-02 17:49:20.163,Kevin Kershaw,2019-03-24 02:49:20.487,196,17,105
6537,2008-09-15 12:11:43.593,FooLman,2016-05-03 12:41:21.573,392,68,45
6851,2008-09-15 12:43:17.807,Fu86,2019-08-31 14:58:03.347,1747,171,243
7736,2008-09-15 14:19:14.253,William Jens,2019-08-30 15:21:39.957,166,14,89
10405,2008-09-15 22:56:19.737,jkinter,2017-10-29 00:00:41.597,68,11,21
11176,2008-09-16 05:53:02.763,afarnham,2019-08-31 01:01:12.84,2155,90,134
13623,2008-09-16 20:14:59.553,Erik,2009-01-03 03:12:02.53,76,1,21
13780,2008-09-16 20:50:27.987,Will Prescott,2019-08-28 11:55:56.297,3665,78,102
```

Congratulations! You had been loaded your first table into HDFS!

# Exercise #01 (15 mins): Load the /tmp/users back from HDFS to PostgreSQL

Command

```
./bin/sqoop export --connect jdbc:postgresql://118.68.168.182:5433/fpt --table users_export --export-dir /tmp/users --username dev --password 12345678
```

Output:

```
19/11/06 22:01:29 INFO sqoop.Sqoop: Running Sqoop version: 1.4.7
19/11/06 22:01:29 WARN tool.BaseSqoopTool: Setting your password on the command-line is insecure. Consider using -P instead.
19/11/06 22:01:29 INFO manager.SqlManager: Using default fetchSize of 1000
19/11/06 22:01:29 INFO tool.CodeGenTool: Beginning code generation
19/11/06 22:01:29 INFO manager.SqlManager: Executing SQL statement: SELECT t.* FROM "users_export" AS t LIMIT 1
19/11/06 22:01:30 INFO orm.CompilationManager: HADOOP_MAPRED_HOME is /usr/hdp/3.0.1.0-187/hadoop-mapreduce
Note: /tmp/sqoop-hdfs/compile/bfced66b36d5bc8e7877b7b427dd4aab/users_export.java uses or overrides a deprecated API.
Note: Recompile with -Xlint:deprecation for details.
19/11/06 22:01:31 INFO orm.CompilationManager: Writing jar file: /tmp/sqoop-hdfs/compile/bfced66b36d5bc8e7877b7b427dd4aab/users_export.jar
19/11/06 22:01:31 INFO mapreduce.ExportJobBase: Beginning export of users_export
19/11/06 22:01:32 INFO client.RMProxy: Connecting to ResourceManager at vm-01.cse87.higio.net/100.64.1.1:8050
19/11/06 22:01:32 INFO client.AHSProxy: Connecting to Application History server at vm-02.cse87.higio.net/100.64.1.2:10200
19/11/06 22:01:33 INFO mapreduce.JobResourceUploader: Disabling Erasure Coding for path: /user/hdfs/.staging/job_1572888198619_0003
19/11/06 22:01:34 INFO input.FileInputFormat: Total input files to process : 4
19/11/06 22:01:34 INFO input.FileInputFormat: Total input files to process : 4
19/11/06 22:01:34 INFO mapreduce.JobSubmitter: number of splits:4
19/11/06 22:01:34 INFO mapreduce.JobSubmitter: Submitting tokens for job: job_1572888198619_0003
19/11/06 22:01:34 INFO mapreduce.JobSubmitter: Executing with tokens: []
19/11/06 22:01:34 INFO conf.Configuration: found resource resource-types.xml at file:/etc/hadoop/3.0.1.0-187/0/resource-types.xml
19/11/06 22:01:34 INFO impl.YarnClientImpl: Submitted application application_1572888198619_0003
19/11/06 22:01:34 INFO mapreduce.Job: The url to track the job: http://VM-01.cse87.higio.net:8088/proxy/application_1572888198619_0003/
19/11/06 22:01:34 INFO mapreduce.Job: Running job: job_1572888198619_0003
19/11/06 22:01:41 INFO mapreduce.Job: Job job_1572888198619_0003 running in uber mode : false
19/11/06 22:01:41 INFO mapreduce.Job:  map 0% reduce 0%
19/11/06 22:01:47 INFO mapreduce.Job:  map 100% reduce 0%
19/11/06 22:01:47 INFO mapreduce.Job: Job job_1572888198619_0003 completed successfully
19/11/06 22:01:47 INFO mapreduce.Job: Counters: 34
	File System Counters
		FILE: Number of bytes read=0
		FILE: Number of bytes written=962788
		FILE: Number of read operations=0
		FILE: Number of large read operations=0
		FILE: Number of write operations=0
		HDFS: Number of bytes read=1131096
		HDFS: Number of bytes written=0
		HDFS: Number of read operations=25
		HDFS: Number of large read operations=0
		HDFS: Number of write operations=0
	Job Counters 
		Launched map tasks=4
		Other local map tasks=1
		Data-local map tasks=2
		Rack-local map tasks=1
		Total time spent by all maps in occupied slots (ms)=37641
		Total time spent by all reduces in occupied slots (ms)=0
		Total time spent by all map tasks (ms)=12547
		Total vcore-milliseconds taken by all map tasks=12547
		Total megabyte-milliseconds taken by all map tasks=38544384
	Map-Reduce Framework
		Map input records=11084
		Map output records=11084
		Input split bytes=767
		Spilled Records=0
		Failed Shuffles=0
		Merged Map outputs=0
		GC time elapsed (ms)=288
		CPU time spent (ms)=6650
		Physical memory (bytes) snapshot=875192320
		Virtual memory (bytes) snapshot=18412871680
		Total committed heap usage (bytes)=727187456
		Peak Map Physical memory (bytes)=222777344
		Peak Map Virtual memory (bytes)=4603875328
	File Input Format Counters 
		Bytes Read=0
	File Output Format Counters 
		Bytes Written=0
19/11/06 22:01:47 INFO mapreduce.ExportJobBase: Transferred 1.0787 MB in 14.825 seconds (74.5085 KB/sec)
19/11/06 22:01:47 INFO mapreduce.ExportJobBase: Exported 11084 records.
```

Check is table users_export have data

```
fpt=> select * from users_export limit 10;
  _id  |      _creationdate      | _displayname  |     _lastaccessdate     | _reputation | _upvotes | _views 
-------+-------------------------+---------------+-------------------------+-------------+----------+--------
   915 | 2008-08-10 11:21:38.537 | Jules         | 2019-01-26 19:05:27.067 |           1 |        0 |    298
  2429 | 2008-08-22 08:48:22.173 | Seb Nilsson   | 2019-08-30 09:43:30.543 |       14387 |      417 |   1124
  4292 | 2008-09-02 17:49:20.163 | Kevin Kershaw | 2019-03-24 02:49:20.487 |         196 |       17 |    105
  6537 | 2008-09-15 12:11:43.593 | FooLman       | 2016-05-03 12:41:21.573 |         392 |       68 |     45
  6851 | 2008-09-15 12:43:17.807 | Fu86          | 2019-08-31 14:58:03.347 |        1747 |      171 |    243
  7736 | 2008-09-15 14:19:14.253 | William Jens  | 2019-08-30 15:21:39.957 |         166 |       14 |     89
 10405 | 2008-09-15 22:56:19.737 | jkinter       | 2017-10-29 00:00:41.597 |          68 |       11 |     21
 11176 | 2008-09-16 05:53:02.763 | afarnham      | 2019-08-31 01:01:12.84  |        2155 |       90 |    134
 13623 | 2008-09-16 20:14:59.553 | Erik          | 2009-01-03 03:12:02.53  |          76 |        1 |     21
 13780 | 2008-09-16 20:50:27.987 | Will Prescott | 2019-08-28 11:55:56.297 |        3665 |       78 |    102
(10 rows)
```