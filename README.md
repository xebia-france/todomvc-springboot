# todomvc-springboot

#### Build the self executable jar
```
spring jar todomvc.jar **/*.groovy
```

#### Reminder to build and push the dockerfile

```
# to build for X86 target
docker build -t jbclaramonte/todomvc-springboot:kube-redis .
docker push jbclaramonte/todomvc-springboot:kube-redis
```

```
# to build and push for ARM target
docker build -f Dockerfile-ARM -t jbclaramonte/todomvc-springboot:kube-redis-arm . && \
docker push jbclaramonte/todomvc-springboot:kube-redis-arm
```

## Run on Kubernetes with Google Container Engine

Start a redis controller with one pods 

```
# for X86 target
kubectl run redis --image=redis

# for ARM target
kubectl run redis --image=hypriot/rpi-redis
```

Expose redis pods as a service

```
kubectl expose rc/redis --port=6379 --target-port=6379
```


Start todomvc controller with one pods

```
# for X86 target
kubectl run todomvc --image=jbclaramonte/todomvc-springboot:kube-redis
```

```
# for ARM target
kubectl run todomvc --image=jbclaramonte/todomvc-springboot:kube-redis-arm
```

Open a shell terminal into the todomvc pod and check the env
 
```
 kubectl exec -it <pod name> bash
```
 
Type env command in the terminal, you should get something looking like this for the response:
 
```
REDIS_PORT_6379_TCP_PROTO=tcp
HOSTNAME=todomvc-qaozd
KUBERNETES_PORT=tcp://10.167.240.1:443
KUBERNETES_PORT_443_TCP_PORT=443
REDIS_SERVICE_PORT=6379
KUBERNETES_SERVICE_PORT=443
KUBERNETES_SERVICE_HOST=10.167.240.1
REDIS_PORT_6379_TCP_ADDR=10.167.252.147
CA_CERTIFICATES_JAVA_VERSION=20140324
REDIS_PORT_6379_TCP_PORT=6379
PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin
PWD=/
JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
LANG=C.UTF-8
JAVA_VERSION=8u72
REDIS_PORT_6379_TCP=tcp://10.167.252.147:6379
SHLVL=1
HOME=/root
JAVA_DEBIAN_VERSION=8u72-b15-1~bpo8+1
REDIS_PORT=tcp://10.167.252.147:6379
KUBERNETES_PORT_443_TCP_PROTO=tcp
REDIS_SERVICE_HOST=10.167.252.147
KUBERNETES_PORT_443_TCP_ADDR=10.167.240.1
KUBERNETES_PORT_443_TCP=tcp://10.167.240.1:443
_=/usr/bin/env
```

> What is important to understand at this point is that the order used to create the different parts is essential : 
> **if you create the todomvc pod BEFORE the redis service the REDIS env won't be available.**


Update the deployed version of todomvc with a new one using _Rolling Update_

```
kubectl rolling-update todomvc --image=jbclaramonte/todomvc-springboot:kube-redis
```
