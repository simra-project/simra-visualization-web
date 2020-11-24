#!/bin/bash
set -e

#get info from user
echo Please enter server name, ex: yourserver.com:
read servername

#add required repositories
sudo apt-get install software-properties-common
sudo apt-add-repository universe
sudo apt update

#install nginx
sudo apt install nginx -y

#install nodejs
sudo apt-get install nodejs -y
sudo apt-get install npm -y

#install git
sudo apt install git -y

#clone repository
git clone https://github.com/simra-project/SimRa-Visualization.git
cd SimRa-Visualization

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
