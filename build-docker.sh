#! /bin/sh

svn update
mvn clean package

docker ps -a | awk '{ print $1,$2 }' | grep magnoabreu/restaurant:1.0 | awk '{print $1 }' | xargs -I {} docker rm -f {}
docker rmi magnoabreu/xplaneproxy:1.0

docker build --tag=magnoabreu/xplaneproxy:1.0 --rm=true .

