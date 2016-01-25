# todomvc-springboot

run
sudo apt-get update
sudo apt-get install -y unzip
sudo apt-get install -y git	
sudo apt-get install -y openjdk-8-jdk

# install nodejs npm
curl -sL https://deb.nodesource.com/setup_4.x | sudo -E bash -
sudo apt-get install -y nodejs
git clone https://github.com/jbclaramonte/todomvc-springboot.git
cd ~/todomvc-springboot/static
npm install

# install spring cli
curl -s http://get.sdkman.io | bash
source "/home/jbclaramonte/.sdkman/bin/sdkman-init.sh"
sdk install springboot


# once everything installed 

cd ~/todomvc-springboot

# you may need to run this command
source "/home/jbclaramonte/.sdkman/bin/sdkman-init.sh"


# run todomvc app:
spring run **/*.groovy -- --spring.redis.host=<redis ip here>


