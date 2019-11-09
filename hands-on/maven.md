# Install Maven (sudo required)

Apache Maven is a software project management and comprehension tool. Based on the concept of a project object model (POM), Maven can manage a project's build, reporting and documentation from a central piece of information.

## Step 01: Make folder for installation:

```
mkdir /opt/mvn
cd /opt/mvn
```

## Step 02: Download maven binary

```
wget http://mirror.downloadvn.com/apache/maven/maven-3/3.6.1/binaries/apache-maven-3.6.1-bin.tar.gz
```

Or 

```
scp member1@118.68.170.134:/opt/apache-maven-3.6.2-bin.tar.gz .
```

## Step 03: Uncompress the binary

```
tar -vzxf apache-maven-3.6.1-bin.tar.gz
```

## Step 04: Add MAVEN_HOME to the end of /etc/profile (system wise)

```
vi /etc/profile

export MAVEN_HOME=/opt/mvn/apache-maven-3.6.1
export PATH=$PATH:$MAVEN_HOME/bin
```

Apply environment

```
. /etc/profile
```

## Step 05: Test if Maven is running

```
mvn -version
```

The output should be

```
Apache Maven 3.6.1 (d66c9c0b3152b2e69ee9bac180bb8fcc8e6af555; 2019-04-05T02:00:29+07:00)
Maven home: /opt/mvn/apache-maven-3.6.1
Java version: 1.8.0_231, vendor: Oracle Corporation, runtime: /opt/java/jdk1.8.0_231/jre
Default locale: en_US, platform encoding: UTF-8
OS name: "linux", version: "5.0.0-31-generic", arch: "amd64", family: "unix"
```