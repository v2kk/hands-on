
Intall JAVA

Create directory

```
mkdir /opt/java
cd /opt/java
```

Download jdk binary

https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html
jdk-8u231-linux-x64.tar.gz

```
https://download.oracle.com/otn/java/jdk/8u231-b11/5b13a193868b4bf28bcb45c792fce896/jdk-8u231-linux-x64.tar.gz?AuthParam=1573262681_44939675a9ad255fdf62068c1eaff434
```

Extract binary 

```
tar -vzxf jdk-8u231-linux-x64.tar.gz
```

Export environment variable

```
vi /etc/profile

export JAVA_HOME=/opt/java/jdk1.8.0_231/
export PATH=$PATH:$JAVA_HOME/bin
```

Test java
```
java -version
```

