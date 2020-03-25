#!/bin/bash
set -e

# Git update
git pull origin master

# Compile Backend with Maven
sudo mvn clean install -DskipTests

# Compile frontend with NPM
cd frontend
npm install
npm run build
