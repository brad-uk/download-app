#
#
#
FROM debian:latest

RUN apt-get update
RUN apt-get install -y software-properties-common python-software-properties

# Install java8
run apt-add-repository -y "deb http://ppa.launchpad.net/webupd8team/java/ubuntu precise main"
run apt-get update
run echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections
run apt-get install -y oracle-java8-installer

#add ready built app jar
copy target/download-app-0.0.1-SNAPSHOT.jar /download-app-0.0.1-SNAPSHOT.jar

#run mkdir /downloads	
#run echo "hello world!" > /downloads/test.txt
#no need for this, run docker with -v /home/brad/Downlaods:/downloads to map host dir

ENTRYPOINT java -jar /download-app-0.0.1-SNAPSHOT.jar --tmp-dir=/tmp --storage-dir=/downloads --user=test --password=password --server.contextPath=/java
EXPOSE 8080
