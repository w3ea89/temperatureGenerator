To start please use docker compose

```bash
docker-compose up -d
```
When container is up please get commands
```bash
docker container exec -it kafka /bin/bash
```
and create kafka topic:
```bash
kafka-topics --bootstrap-server localhost:9092 --topic temperatureMeasurements --create --partitions 1
```
