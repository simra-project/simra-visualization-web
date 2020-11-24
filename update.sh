#!/bin/bash
set -e

# Git update
git pull origin master

# Compile frontend with NPM
cd frontend
npm install
npm run build
