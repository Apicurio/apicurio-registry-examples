## Commands

```
docker-compose -f demo-infra.yaml up
./scripts/setup-registry.sh
```

```
cd spring-example
mvn generate-sources -Pavro
mvn package
java -jar target/apicurio-kafka-demo-spring-0.0.1-SNAPSHOT.jar
```

```
cd quarkus-example
mvn generate-sources -Pavro
mvn quarkus:dev
```

```
cd quarkus-kafka-streams/
mvn generate-sources -Pavro
mvn quarkus:dev
```

```
kafkacat -b localhost:9092 -t events
http :8081/kafka/publish name=hello description=spring
http :8082/kafka/publish name=hello description=quarkus
http :8083/orders/book/5
```