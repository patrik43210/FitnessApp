# POSTGES SETUP
docker run --name fitnessapp-postgres -e POSTGRES_USER=patrik -e POSTGRES_PASSWORD=secret -e POSTGRES_DB=fitnessapp_db -p 5432:5432 -d postgres

# MONGO SETUP
docker run -d --name mongodb -p 27017:27017 -e MONGO_INITDB_ROOT_USERNAME=patrik -e MONGO_INITDB_ROOT_PASSWORD=secret mongo:latest
or
docker run -d --name mongodb -p 27017:27017 mongo:latest

# latest RabbitMQ 4.x -deletable -- guest guest
docker run -it --rm --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:4-management

or
# latest RabbitMQ 4.x --- guest guest
docker run -it --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:4-management

# ACCESS LINKS

# EUREKA
http://localhost:8761

# User service
http://localhost:8081/swagger-ui/index.html

# Activity service
http://localhost:8082/swagger-ui/index.html

# AI service
http://localhost:8083/swagger-ui/index.html
