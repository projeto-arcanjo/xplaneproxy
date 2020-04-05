#! /bin/sh

mvn clean package

docker ps -a | awk '{ print $1,$2 }' | grep projetoarcanjo/xplaneproxy:1.0 | awk '{print $1 }' | xargs -I {} docker rm -f {}
docker rmi projetoarcanjo/xplaneproxy:1.0

docker build --tag=projetoarcanjo/xplaneproxy:1.0 --rm=true .

