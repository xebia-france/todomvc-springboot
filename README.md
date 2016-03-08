# todomvc-springboot

#### Build the self executable jar
```
spring jar todomvc.jar **/*.groovy
```

#### Reminder to build and push the dockerfile
```
# to build for X86 target
docker build -f Dockerfile -t jbclaramonte/todomvc-springboot:kube-sql . && \
docker push jbclaramonte/todomvc-springboot:kube-sql

# to build and push for ARM target
docker build -f Dockerfile-ARM -t jbclaramonte/todomvc-springboot:kube-sql-arm . && \
docker push jbclaramonte/todomvc-springboot:kube-sql-arm
```

## Run on Kubernetes with Google Container Engine

First start an sql database using sql instance from GCE

Once the database is up and running create a service pointing out to the sql server
(see http://kubernetes.io/v1.1/docs/user-guide/services.html#services-without-selectors)

```
kubectl create -f k8s/service-sql.yml
kubectl create -f k8s/service-sql-endpoint.yml
```

We can check if we have access to our database from our kubernetes cluster and by using the ip provided by the service:
```
# run a pod with mysql in it
kubectl create -f k8s/simple-sql-client.yml
 
# connect and run a bash inside the pod
kubectl exec -it simple-sql-client bash

# use the mysql client to connect to the server using the env set inside the pod
mysql -h"$EXTERNAL_MYSQL_SERVICE_HOST" -P"$EXTERNAL_MYSQL_SERVICE_PORT" -uroot -p"adminpassword"
mysql -h"$EXTERNAL_MYSQL_SERVICE_HOST" -P"$EXTERNAL_MYSQL_SERVICE_PORT" -utodoapp -p"p455word"
```


Start todomvc controller with one pod

```
kubectl run todomvc-sql --image=jbclaramonte/todomvc-springboot:kube-sql
```

Exposes it to the world
```
kc expose rc todomvc-sql --port=80 --target-port=8080 
```