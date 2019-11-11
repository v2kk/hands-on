# Wordcount Example

## Objectives of this labs
- Count the frequency of words in files
- How to run MapReduce on local machine
- How to submit MapReduce on cluster
- How to check if it's running

## Required Environment
- [Java](https://github.com/vinhdangphuc/hands-on/blob/master/hands-on/java.md)
- [Maven](https://github.com/vinhdangphuc/hands-on/blob/master/hands-on/maven.md)

## Clone source code

```
git clone https://github.com/vinhdangphuc/labs-mapreduce.git
```

Move to wordcount folder

```
cd labs-mapreduce/wordcount/
```

## Run Local

- Please, remove folder `output` inside project root folder if existed

```
mvn install exec:java -Dexec.mainClass="vn.fpt.App" -Dexec.args="./input ./output"
```

## Run on Hadoop

### Build executable jar using maven

```
mvn package


[INFO] --- maven-jar-plugin:2.4:jar (default-jar) @ wordcount ---
[INFO] Building jar: /home/vinhdp/workspaces/fpt/training/labs-mapreduce/wordcount/target/wordcount-1.0-SNAPSHOT.jar
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 2.474 s
[INFO] Finished at: 2019-11-09T19:17:48+07:00
[INFO] ------------------------------------------------------------------------
```

### Copy jar in to a node in hadoop cluster

```
scp target/wordcount-1.0-SNAPSHOT.jar member1@118.68.170.134:~/
```

### Submit job on cluster

Login into dev cluster using provided user and password

```
ssh member1@118.68.170.134
```

And execute command

```
hadoop jar wordcount-1.0-SNAPSHOT.jar vn.fpt.App /data/wordcount/input /data/wordcount/output
```

Check if it is running on yarn with your submission job id: application_1572888198619_0043

```
yarn top


YARN top - 19:26:18, up 4d, 19:2, 0 active users, queue(s): root
NodeManager(s): 3 total, 3 active, 0 unhealthy, 0 decommissioned, 0 lost, 0 rebooted
Queue(s) Applications: 1 running, 39 submitted, 0 pending, 38 completed, 0 killed, 0 failed
Queue(s) Mem(GB): 24 available, 3 allocated, 0 pending, 0 reserved
Queue(s) VCores: 17 available, 1 allocated, 0 pending, 0 reserved
Queue(s) Containers: 1 allocated, 0 pending, 0 reserved

                  APPLICATIONID USER             TYPE      QUEUE PRIOR   #CONT  #RCONT  VCORES RVCORES     MEM    RMEM  VCORESECS    MEMSECS %PROGR       TIME NAME                                        
 application_1572888198619_0043 member1     mapreduce    default     0       1       0       1       0      3G      0G          3          9   0.00   00:00:00 Word Count

```

Check id existed in [Resource Manager UI](http://118.68.170.134:8088/cluster)

Finally, check the output

```
hdfs dfs -cat /data/wordcount/output/*

A	2
B	3
C	2
X	2
```

Congratulations! You don't make any problems until now ^^!

