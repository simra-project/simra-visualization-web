#!/bin/bash
set -e

#get info from user
echo Please enter server name, ex: yourserver.com:
read servername
echo Please specify the directory to be monitored by the importer:
read monitordir
echo Please specify the logging directory:
read loggingdir
echo Please specify the user you want to run the java applications on:
read javauser

#add required repositories
sudo apt-get install software-properties-common
sudo apt-add-repository universe
sudo apt update

#install nginx
sudo apt install nginx -y

#install java + maven
sudo apt-get install default-jre -y
sudo apt-get install default-jdk -y
sudo apt install maven -y

#install nodejs
sudo apt-get install nodejs -y
sudo apt-get install npm -y

#install mongodb
. /etc/lsb-release
sudo apt-get install gnupg -y
wget -qO - https://www.mongodb.org/static/pgp/server-4.2.asc | sudo apt-key add -
if [ "$DISTRIB_CODENAME" = "xenial" ]
then
	echo "deb [ arch=amd64,arm64 ] https://repo.mongodb.org/apt/ubuntu xenial/mongodb-org/4.2 multiverse" | sudo tee /etc/apt/sources.list.d/mongodb-org-4.2.list
else
	echo "deb [ arch=amd64,arm64 ] https://repo.mongodb.org/apt/ubuntu bionic/mongodb-org/4.2 multiverse" | sudo tee /etc/apt/sources.list.d/mongodb-org-4.2.list
fi
sudo apt update
sudo apt install mongodb-org -y
sudo systemctl daemon-reload
sudo systemctl start mongod

#install git
sudo apt install git -y

#clone repository
git clone https://github.com/simra-project/SimRa-Visualization.git
cd SimRa-Visualization

#update configuration of CSV importer
sudo cat <<EOT >> backend/csvimporter/src/main/resources/application.properties
csv.monitor.path=$monitorpath
logging.file.name=$loggingdir/csvimporter.log
EOT

#compile jars
sudo mvn clean install -DskipTests

#import dump
mongorestore

#compile frontend
cd frontend
npm install
npm run build
cd ..

#configure and start frontend
sudo cat <<EOT > /etc/nginx/sites-enabled/simra.conf
server {
  listen 80;
  server_name $servername;

  # Tell Nginx where your app's 'public' directory is
  root `pwd`/frontend/dist;

  location / {
	try_files \$uri \$uri/ /index.html;
  }
}
EOT
sudo service nginx restart

#configure and start interface
sudo cat <<EOT > /etc/systemd/system/simra_api.service
[Unit]
Description=Simra Springboot API
After=syslog.target
After=network.target[Service]
User=$javauser
Type=simple

[Service]
ExecStart=/usr/bin/java -jar `pwd`/backend/SimRa-Visualization-API/target/SimRa-Visualization-API-1.0-SNAPSHOT.jar
Restart=always
StandardOutput=syslog
StandardError=syslog
SyslogIdentifier=simra_api

[Install]
WantedBy=multi-user.target
EOT
sudo systemctl start simra_api

#configure and start backend

sudo cat <<EOT > /etc/systemd/system/simra_backend.service
[Unit]
Description=Simra Springboot API
After=syslog.target
After=network.target[Service]
User=$javauser
Type=simple

[Service]
ExecStart=/usr/bin/java -jar `pwd`/backend/csvimporter/target/csvimporter-0.0.1-SNAPSHOT.jar
Restart=always
StandardOutput=syslog
StandardError=syslog
SyslogIdentifier=simra_backend

[Install]
WantedBy=multi-user.target
EOT
sudo systemctl start simra_backend
