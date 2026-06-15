# Expenz

## Local setup

### Requirements

- **Java** version 21
- **PostgreSQL** version 18.3
- **Redis**

### Steps to run the application

Ensure the **PostgreSQL** server is running

```shell
sudo service postgresql status
```

Create the **expenz** database in your **PostgreSQL** locally

```shell
CREATE DATABASE Expenz;
```

Build the **Expenz** application using **Gradle**

```shell
./gradlew clean build
```

Now run the **Jar** file

```shell
java -jar build/libs/Expenz-0.0.1-SNAPSHOT.jar
```

## Docker setup

### Requirements

- **Java** version 21
- **Docker**

### Steps to run the application

Ensure the **Docker engine** is running

```shell
docker version
```

Comment the below lines in the **application.properties** file

```text
## PostgreSQL - Local connection
spring.datasource.url=jdbc:postgresql://localhost:5432/expenz
spring.datasource.username=root
spring.datasource.password=root
```

Uncomment the below lines in the **application.properties** file to use the Docker containers connection

```text
## PostgreSQL - Docker container connection
#spring.datasource.url=jdbc:postgresql://postgres:5432/expenz
#spring.datasource.username=user
#spring.datasource.password=password

## Redis - Docker container connection
#spring.data.redis.host=redis
#spring.data.redis.port=6379
#spring.data.redis.password=
```

Build the **Expenz** application using **Gradle**

```shell
./gradlew clean build
```

Launch the **docker-compose.yml** file to Dockerize the application

```shell
docker compose build
docker compose up
```

Ensure the **Expenz application**, **PostgreSQL** and **Redis** containers are present in list

```shell
docker ps
```

If you want to execute PostgreSQL commands, connect to the **PostgreSQL container** bash

```shell
docker exec -it postgres /bin/bash
pqsl -U user -ppassword
```

## REST APIs

### Users APIs

Create a new User record

```shell
curl --location --request POST 'localhost:8080/user/register' \
--header 'Accept: application/json' \
--header 'Content-Type: application/json' \
--data-raw '{
    "username" : "Kang",
    "email" : "kang@marvel.com",
    "password" : "kangtheconqueror"
}'
```

Fetch the authenticated user **JWT** token. Then copy the token from response header.

```shell
curl --location --request POST 'localhost:8080/authenticate' \
--header 'Content-Type: application/json' \
--data '{
    "username" : "Kang",
    "password" : "kangtheconqueror"
}'
```

Fetch a User record by ID

```shell
curl --location 'localhost:8080/user/{{user-id}}' \
--header 'Authorization: Bearer {{JWT Token}}'
```

Update a User fields by ID

```shell
curl --location --request PATCH 'localhost:8080/user/{{user-id}}' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer {{JWT Token}}' \
--data-raw '{
    "email" : "kangtheconqueror@marvel.com"
}'
```

Delete a User record by ID

```shell
curl --location --request DELETE 'localhost:8080/user/{{user-id}}' \
--header 'Authorization: Bearer {{JWT Token}}'
```

### Expenses APIs

Fetch Expense and Income records by month and year

```shell
curl --location 'localhost:8080/expense/all?month=1&year=2024' \
--header 'Authorization: Bearer {{JWT Token}}'
```

Fetch Expense records by month and year

```shell
curl --location 'localhost:8080/expense/expenses?month=1&year=2024' \
--header 'Authorization: Bearer {{JWT Token}}'
```

Fetch Income records by month and year

```shell
curl --location 'localhost:8080/expense/incomes?month=1&year=2024' \
--header 'Authorization: Bearer {{JWT Token}}'
```

Fetch an Expense record by ID

```shell
curl --location 'localhost:8080/expense/{{expense-id}}' \
--header 'Authorization: Bearer {{JWT Token}}'
```

Create a new Expense record

```shell
curl --location 'localhost:8080/expense' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer {{JWT Token}}' \
--data '{
    "amount": 100.0,
    "title": "Buy Green Apples",
    "description": "Buy some Green Apples to eat",
    "category": "grocery",
    "isItIncome": false,
    "dateAdded": "2023-12-25T12:00:00.000+00:00"
}
'
```

Update an Expense fields by ID

```shell
curl --location --request PATCH 'localhost:8080/expense/{{expense-id}}' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer {{JWT Token}}' \
--data '{
    "description": "To eat while talking to Loki variants"
}
'
```

Delete an Expense record by ID

```shell
curl --location --request DELETE 'localhost:8080/expense/{{expense-id}}' \
--header 'Authorization: Bearer {{JWT Token}}'
```
