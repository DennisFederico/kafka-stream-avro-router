# ROUTING DATA WITHOUT ALTERING THE SOURCE SCHEMA

## PREPARE CREDENTIALS

Create credentials in CCloud or other cluster and update config/connection.properties

```bash
### KAFKA
+---------+------------------------------------------------------------------+
| API Key | MY_KAFKA_API_KEY                                                 |
| Secret  | MY_KAFKA_API_KEY_SECRET                                          |
+---------+------------------------------------------------------------------+

### SCHEMA-REGISTRY
+---------+------------------------------------------------------------------+
| API Key | MY_SR_API_KEY                                                    |
| Secret  | MY_SR_API_KEY_SECRET                                             |
+---------+------------------------------------------------------------------+
```

## CREATE TEST SCHEMA (AVRO)

Schema is available in the `schema` folder

https://docs.confluent.io/platform/current/schema-registry/develop/api.html

```bash
## USING CONFLUENT CLI
confluent schema-registry schema create --schema schema/personData.avsc --subject PersonWithBirthYear --type AVRO

## USING CURL/REST
curl -X POST -H "Content-Type: application/json" --data @person-sr.json http://localhost:8081/subjects/person/version
```

## PREPARE TOPICS

```bash
confluent kafka topic create PersonWithBirthYear --partitions 1
confluent kafka topic create Person_Default --partitions 1
confluent kafka topic create Person_GenX --partitions 1
confluent kafka topic create Person_Millennials --partitions 1
```



## PRODUCE DATA

```bash
confluent kafka topic produce PersonWithBirthYear --value-format avro --schema schema/personData.avsc

{ "PERSON_ID": 789, "Name":"Dennis", "LastName":"Federico", "BirthYear": 1980, "Nationality": { "COUNTRY_ID": 34, "CountryName":"GenX" } }
{ "PERSON_ID": 790, "Name":"Abigail", "LastName":"Monroe", "BirthYear": 1990, "Nationality": { "COUNTRY_ID": 34, "CountryName":"Millennial" } }
{ "PERSON_ID": 791, "Name":"Franceso", "LastName":"Zapata", "BirthYear": 2000, "Nationality": { "COUNTRY_ID": 1, "CountryName":"Default" } }
{ "PERSON_ID": 792, "Name":"Giordanno", "LastName":"Schiavo", "BirthYear": 1977, "Nationality": { "COUNTRY_ID": 1, "CountryName":"GenX" } }
{ "PERSON_ID": 792, "Name":"Alpha", "LastName":"Charlie", "BirthYear": 1981, "Nationality": { "COUNTRY_ID": 1, "CountryName":"Millenial" } }
{ "PERSON_ID": 792, "Name":"Elvis", "LastName":"Sandovar", "BirthYear": 1961, "Nationality": { "COUNTRY_ID": 1, "CountryName":"Default" } }

confluent kafka topic consume PersonWithBirthYear --value-format avro -b
```

## RUN ROUTER

```bash
## DOCKER
docker build . -t dfederico/generation-router:1.0
docker run --name router dfederico/generation-router:1.0

## COMMANDLINE
java -jar target/person-generation-router-jar-with-dependencies.jar config/connection.properties config/application.yml
```

## RESET CONSUMER GROUP

```bash
kafka-consumer-groups --bootstrap-server pkc-4r297.europe-west1.gcp.confluent.cloud:9092 \
  --command-config config/connection.properties \
  --reset-offsets \
  --to-earliest \
  --group kstreams.router \
  --all-topics \
  --execute
```
