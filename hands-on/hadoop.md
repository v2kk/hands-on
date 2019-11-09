# Hands-On Exercise: Using Hadoop Distributed File System (HDFS)

In this lab, you will:
    
    - Install a Hadoop Pseudo-Distributed Mode on a single machine.
    - Working with Hadoop primary components interface
    - Practice some HDFS commands dealing with file.

## Step #01: Prerequsities

- [Install Java](https://github.com/vinhdangphuc/hands-on/blob/master/hands-on/java.md)
- Install Open SSH server

```
sudo apt-get install openssh-server
```

Start SSH server

```
sudo service sshd start
```

## Step #02: Create working directory & change current directory

```
mkdir /tmp/hadoop
cd /tmp/hadoop

```

## Step #03: Download Hadoop Binary Archive

```
wget https://archive.apache.org/dist/hadoop/core/hadoop-3.1.1/hadoop-3.1.1.tar.gz
```

Or

```
scp member1@118.68.170.134:/opt/hadoop-3.1.1.tar.gz .
```

Extract hadoop binary

```
tar -vzxf hadoop-3.1.1.tar.gz
cd hadoop-3.1.1/

```

## Step #04: Setup Hadoop Pseudo-Distributed Mode

### 4.1 Setup Hadoop Environment Variables

Setup the environment variables used by the Hadoop. Edit ~/.bashrc file and append following values at end of file.

```
export HADOOP_HOME=/tmp/hadoop/hadoop-3.1.1
```

Then, apply the changes in the current running environment

```
source ~/.bashrc
```

Now set JAVA_HOME in $HADOOP_HOME/etc/hadoop/hadoop-env.sh file to your JAVA path location

```
vim $HADOOP_HOME/etc/hadoop/hadoop-env.sh
```

### 4.2 Setup Hadoop Configuration Files
Let’s start with the configuration with basic Hadoop single node cluster setup. first, navigate to below location

```
cd $HADOOP_HOME/etc/hadoop
```

Edit core-site.xml
```
<configuration>
    <property>
        <name>fs.default.name</name>
        <value>hdfs://localhost:9000</value>
    </property>
</configuration>
```

Edit hdfs-site.xml

```
<configuration>
    <property>
        <name>dfs.replication</name>
        <value>1</value>
    </property>

    <property>
        <name>dfs.name.dir</name>
        <value>file:///tmp/hadoop/data/namenode</value>
    </property>

    <property>
        <name>dfs.data.dir</name>
        <value>file:///tmp/hadoop/data/datanode</value>
    </property>
</configuration>
```

Edit mapred-site.xml

```
<configuration>
    <property>
        <name>mapreduce.framework.name</name>
        <value>yarn</value>
    </property>
</configuration>
```

Edit yarn-site.xml

```
<configuration>
 <property>
  <name>yarn.nodemanager.aux-services</name>
    <value>mapreduce_shuffle</value>
 </property>
</configuration>
```

### 4.3 Format Namenode

Move to bin folder

```
cd $HADOOP_HOME/bin
```

To delete all metadata on Namenode, all the information on the datanodes will be deleted and they can be reused for new data

```
./hdfs namenode -format
```

Sample output:

```
2019-11-05 21:12:19,695 INFO namenode.NameNode: STARTUP_MSG: 
/************************************************************
STARTUP_MSG: Starting NameNode
STARTUP_MSG:   host = vinhdp/127.0.1.1
STARTUP_MSG:   args = [-format]
STARTUP_MSG:   version = 3.1.1
...
STARTUP_MSG:   build = https://github.com/apache/hadoop -r 2b9a8c1d3a2caf1e733d57f346af3ff0d5ba529c; compiled by 'leftnoteasy' on 2018-08-02T04:26Z
STARTUP_MSG:   java = 1.8.0_211
************************************************************/
2019-11-05 21:12:19,701 INFO namenode.NameNode: registered UNIX signal handlers for [TERM, HUP, INT]
2019-11-05 21:12:19,703 INFO namenode.NameNode: createNameNode [-format]
Formatting using clusterid: CID-5a997c51-9f15-4b98-9cee-6a35d2a48103
2019-11-05 21:12:20,114 INFO namenode.FSEditLog: Edit logging is async:true
...
2019-11-05 21:12:20,333 INFO namenode.FSImage: Allocated new BlockPoolId: BP-745642687-127.0.1.1-1572963140328
2019-11-05 21:12:20,342 INFO common.Storage: Storage directory /tmp/hadoop/data/namenode has been successfully formatted.
2019-11-05 21:12:20,351 INFO namenode.FSImageFormatProtobuf: Saving image file /tmp/hadoop/data/namenode/current/fsimage.ckpt_0000000000000000000 using no compression
2019-11-05 21:12:20,447 INFO namenode.FSImageFormatProtobuf: Image file /tmp/hadoop/data/namenode/current/fsimage.ckpt_0000000000000000000 of size 391 bytes saved in 0 seconds .
2019-11-05 21:12:20,454 INFO namenode.NNStorageRetentionManager: Going to retain 1 images with txid >= 0
2019-11-05 21:12:20,457 INFO namenode.NameNode: SHUTDOWN_MSG: 
/************************************************************
SHUTDOWN_MSG: Shutting down NameNode at vinhdp/127.0.1.1
************************************************************/

```

## Step #05: Start Hadoop Cluster

Move to sbin folder

```
cd $HADOOP_HOME/sbin/
```

Start HDFS

```
./start-dfs.sh


Starting namenodes on [localhost]
Starting datanodes
Starting secondary namenodes [vinhdp]
```

Check if HDFS is running

```

```

Start YARN

```
./start-yarn.sh


Starting resourcemanager
Starting nodemanagers
```

Check if YARN is running

```

```

## Step #07: Access Hadoop Services in Browser

### 7.1 HDFS Name Node

http://localhost:9870

### 7.2 YARN Resource Manager

http://localhost:8088/cluster

### 7.3 YARN Node Manager

http://localhost:8042/node

## Step #08: Test Hadoop Single Node Setup

Move to Hadoop bin directory

```
cd $HADOOP_HOME/bin
```

### 8.1 Make new hdfs directory
```
./hdfs dfs -mkdir /data
./hdfs dfs -mkdir /data/example

./hdfs dfs -ls /data
```

### 8.2 Copy file from local disk to HDFS

```
./hdfs dfs -put /tmp/example.csv /data/
```

### 8.3 Cat data in HDFS

```
./hdfs dfs -cat /data/example.csv
```

### 8.3 Copy data from HDFS to local disk

```
./hdfs dfs -get /data/example.csv ./
head ./example.csv
```

## Step #09: Stop Hadoop

```
cd $HADOOP_HOME/sbin

./stop-yarn.sh
./stop-hdfs.sh
```
