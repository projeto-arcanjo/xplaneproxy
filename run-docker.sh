#! /bin/sh

docker run --name xplaneproxy --hostname=xplaneproxy --network arcanjo \
    -e FEDERATION_NAME=ArcanjoFederation \
    -e FEDERATE_NAME=XPlaneProxy \
	-v /etc/localtime:/etc/localtime:ro \
	-p 36000:8080 \
	-p 49003:49003/udp \
	-d magnoabreu/xplaneproxy:1.0	



