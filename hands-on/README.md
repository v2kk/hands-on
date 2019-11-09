# Passwordless SSH

Add the following content to your `~/.bash_aliases`

```
vi ~/.bash_aliases

ssh_to_dev_server() {
        server=$1
        user=$2

        [[ -z "$user" ]] && echo "user is missing!" && return || echo "user is $user"
        [[ -z "$server" ]] && echo "server is missing!" && return || echo "server is $server"

        if [ "$server" == "vm1" ]; then
                host="118.68.170.134"
        elif [ "$server" == "vm2" ]; then
                host="118.68.168.182"
        elif [ "$server" == "vm3" ]; then
                host="118.68.170.148"
        else
                echo "================>"
                echo "Unknow server"
                return
        fi

        echo "===============>"
        echo "Connecting to server $server ($host) by user $user"
        ssh $user'@'$host
}

alias se='ssh_to_dev_server $@'
```

Apply environment

```
. ~/.bash_aliases
```

Now, you can be able to use command as (prompt for password)

```
se $server $user
```

With:

- $server in: vm1, vm2, vm3
- $user in: member1/member2/member3/member4/member5

You can copy the public key into the server to emit password prompt for next time

```
ssh-copy-id user@ip
```

Input the password and output should be

```
ssh-copy-id member2@118.68.168.182


/usr/bin/ssh-copy-id: INFO: attempting to log in with the new key(s), to filter out any that are already installed
/usr/bin/ssh-copy-id: INFO: 1 key(s) remain to be installed -- if you are prompted now it is to install the new keys
member2@118.68.168.182's password: 

Number of key(s) added: 1

Now try logging into the machine, with:   "ssh 'member2@118.68.168.182'"
and check to make sure that only the key(s) you wanted were added.
```

Now, try with

```
se vm2 member2
```

Output should be

```
user is member2
server is vm2
===============>
Connecting to server vm2 (118.68.168.182) by user member2
Last login: Sat Nov  9 20:59:02 2019 from 183.80.132.70
```

Ok, let's go!