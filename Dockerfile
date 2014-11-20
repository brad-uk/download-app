
FROM debian:latest
	
#copy JRE in

#note COPY can only work on files inside build dir
COPY server-jre-8u25-linux-x64.tar.gz /tmp/server-jre-8u25-linux-x64.tar.gz

#extract it, chown & delete up archive
RUN tar xzf /tmp/server-jre-8u25-linux-x64.tar.gz -C /opt && \
	chown root:root -R /opt/jdk1.8.0_25 && \
	rm -fr /tmp/server-jre-8u25-linux-x64.tar.gz

ENV JAVA_HOME /opt/jdk1.8.0_25
ENV PATH $PATH:$JAVA_HOME/bin

#add ready built app jar
copy target/download-app-0.0.1-SNAPSHOT.jar /download-app-0.0.1-SNAPSHOT.jar

ENTRYPOINT java \
		-Dakka.remote.netty.tcp.bind-hostname=0.0.0.0 \
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
			
EXPOSE 8080
EXPOSE 2551
