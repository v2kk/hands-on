
# Intall JAVA (sudo required)

Create directory

```
mkdir /opt/java
cd /opt/java
```

Download jdk binary

```
scp member1@118.68.170.134:/opt/jdk-8u231-linux-x64.tar.gz .
```

Extract binary 

```
tar -vzxf jdk-8u231-linux-x64.tar.gz
```

Export environment variable in the end of file

```
vi /etc/profile

export JAVA_HOME=/opt/java/jdk1.8.0_231/
export PATH=$PATH:$JAVA_HOME/bin
```

Apply environment

```
. /etc/profile
```

Test java
```
java -version
```

Output should be

```
java version "1.8.0_231"
...
```

