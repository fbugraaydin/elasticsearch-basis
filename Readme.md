## DevsMeet ElasticSearch Basis Meetup
This project simulates product management and searching APIs in e-commerce websites to understand ElasticSearch.

## Tech / Framework

- [Spring](https://spring.io/)
- [ElasticSearch](https://www.elastic.co/)  
- [Kotlin](https://kotlinlang.org/)
- [h2](https://mvnrepository.com/artifact/com.h2database/h2)
- [Gradle](https://gradle.org/)
- [MapStruct](https://mapstruct.org/)
- [SpringDoc](https://springdoc.org/)

## Features
- CRUD APIs on database
- Search on ElasticSearch
- Filter on ElasticSearch

## Prerequisites
- Install Docker & Docker Compose
- Install JDK 11+
- Clone this github project and switch to **elasticsearch-kibana** branch that includes docker-compose file to run ElasticSearch and Kibana.
```bash
git clone https://github.com/fbugraaydin/elasticsearch-docker.git
```
- Then, run this command to have ElasticSearch and Kibana on your local environment.
```bash
docker-compose up
```

## Let's Run
Project runs on default port :8080.

- To see APIs on swagger-ui: http://localhost:8080/swagger-ui/index.html
- To connect h2 database: http://localhost:8080/h2-console/
- To connect Kibana: http://localhost:5601
- To connect ElasticSearch: http://localhost:9200

## Documentation
- To understand ELK stack and ElasticSearch features in detail, you can look at prepared documentation [ElasticSearch](ElasticSearch.pdf)
- To apply capabilities of ElasticSearch, you can look at prepared commands in [Commands](basis-commands.json)


## Licence
Developed by © [Fuat Buğra AYDIN](https://www.linkedin.com/in/fuatbugraaydin/)