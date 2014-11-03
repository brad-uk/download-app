#!/bin/bash

#possible use for local testing, won't work with coreos nodes though

#get ip address of interface docker0 which should be the docker bridge interface?
export NODE_ADDR=ip addr show dev docker0 | awk '/inet / { print $2 }'

java \
	-Dakka.remote.netty.tcp.hostname=$NODE_ADDR \
	-Dakka.remote.netty.tcp.port=$NODE_PORT \
	-Dakka.cluster.seed-nodes.0=akka.tcp://ClusterSystem@$SEED_0_ADDR:$SEED_0_PORT \
	-Dakka.cluster.seed-nodes.1=akka.tcp://ClusterSystem@$SEED_1_ADDR:$SEED_1_PORT \
	-jar /download-app-0.0.1-SNAPSHOT.jar \
	--tmp-dir=/tmp \
	--storage-dir=/downloads \
	--user=test \
	--password=password \ 
	--server.contextPath=/java \
	--server.port=$HTTP_PORT
