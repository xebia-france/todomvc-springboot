# todomvc-springboot

@
## Run locally
You should have the Spring cli, you can install it easily

```bash
# get the "Software Development Kit Manager" (sdkman) to install the cli
curl -s http://get.sdkman.io | bash
source "/home/jbclaramonte/.sdkman/bin/sdkman-init.sh"

# install springboot cli using sdkman
sdk install springboot
```

You should also have npm installed to install all the js dependency needed for the angularjs app

```bash
cd ~/todomvc-springboot/static
npm install
```

Once done you can run an instance of redis using docker:

```bash
docker run --name redis -d -p 6379:6379 redis
```

Finally run the todomvc app:

```bash
spring run **/*.groovy -- --spring.redis.host=<redis ip here>
```

Open http://localhost:8080

You can also test the api using swagger with http://localhost:8080/swagger-ui.html 

## Run on a GCE

Note that in this case you will also have to run a redis instance in GCE (you can do that with the Cloud Launcher)

Execute the shell script which installs everything you need:
```bash
./install-gce
```

Once everything installed : 

```bash
cd ~/todomvc-springboot

# you may need to run this command
source "~/.sdkman/bin/sdkman-init.sh"

# run todomvc app:
spring run **/*.groovy -- --spring.redis.host=<redis ip here>
```


