#! /bin/sh

docker run --name xplaneproxy --hostname=xplaneproxy --network arcanjo \
    -e FEDERATION_NAME=ArcanjoFederation \
    -e FEDERATE_NAME=XPlaneProxy \
    -e XPLANE_ADDRESS=192.168.0.76 \
	-v /etc/localtime:/etc/localtime:ro \
	-p 36000:8080 \
	-p 49003:49003/udp \
	-d projetoarcanjo/xplaneproxy:1.0	



