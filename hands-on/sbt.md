# Install Sbt (sudo required)

Create sbt root directory

```
mkdir /opt/sbt
cd /opt/sbt
```

Download sbt binary

```
wget https://piccolo.link/sbt-1.3.3.tgz
```

Or

```
scp member1@118.68.170.134:/opt/sbt-1.3.3.tgz .
```

Extract binary

```
tar -vzxf sbt-1.3.3.tgz
```

Export environment variable in the end of file

```
vi /etc/profile

export SBT_HOME=/opt/sbt
export PATH=$PATH:$SBT_HOME/bin
```

Apply environment

```
. /etc/profile
```

Test sbt
```
sbt sbtVersion
```

Output should be

```
[info] Loading settings for project global-plugins from metals.sbt ...
[info] Loading global plugins from /home/vinhdp/.sbt/1.0/plugins
[info] Loading project definition from /home/vinhdp/project
[info] Set current project to vinhdp (in build file:/home/vinhdp/)
[info] 1.3.3
```