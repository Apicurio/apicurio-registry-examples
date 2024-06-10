# docker build -t="apicurio/kafka-all" --rm .
# docker run -it -p 9092:9092 -p 9091:9091 -p 2181:2181 apicurio/kafka-all
# docker run -it -p 8080:8080 quay.io/apicurio/apicurio-registry-mem:latest-snapshot
# (Depending on your network configuration, add `--net host` option
# to the `docker` commands if required to make services reachable)
FROM quay.io/centos/centos:stream8

RUN yum update -y && \
    yum install -y java-1.8.0-openjdk-devel && \
    curl https://archive.apache.org/dist/kafka/2.8.1/kafka_2.12-2.8.1.tgz -o /tmp/kafka.tgz && \
    tar xfz /tmp/kafka.tgz -C /usr/local && \
    mv /usr/local/kafka_2.12-2.8.1 /usr/local/kafka

RUN echo "#!/bin/sh" >> /usr/local/kafka/start_kafka.sh && \
    echo "cd /usr/local/kafka" >> /usr/local/kafka/start_kafka.sh && \
    echo "./bin/zookeeper-server-start.sh config/zookeeper.properties &" >> /usr/local/kafka/start_kafka.sh && \
    echo "sleep 5" >> /usr/local/kafka/start_kafka.sh && \
    echo "./bin/kafka-server-start.sh config/server.properties" >> /usr/local/kafka/start_kafka.sh && \
    chmod 755 /usr/local/kafka/start_kafka.sh

EXPOSE 9092
EXPOSE 9091
EXPOSE 2181
EXPOSE 2888

CMD /usr/local/kafka/start_kafka.sh
