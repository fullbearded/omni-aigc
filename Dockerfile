# build front-end
FROM node:lts-alpine AS frontend

RUN npm install pnpm -g

WORKDIR /app

COPY ./package.json /app

COPY ./pnpm-lock.yaml /app

RUN pnpm install

COPY . /app

RUN pnpm run build

# build backend
FROM maven:3.8.3-openjdk-17 as backend

WORKDIR /app

COPY /server /app

RUN mvn clean package -DskipTests=true

# service
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY --from=backend /app/target/server-0.0.1-SNAPSHOT.jar /app
COPY --from=frontend /app/dist /app/public

# 安装 nginx
RUN apt-get update && apt-get install -y nginx

COPY ./docker-compose/server.conf /etc/nginx/conf.d
COPY ./docker-compose/nginx.conf /etc/nginx


EXPOSE 80

CMD service nginx start && java -jar server-0.0.1-SNAPSHOT.jar
