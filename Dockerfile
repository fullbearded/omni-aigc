# build front-end
FROM node:lts-alpine AS frontend

RUN npm install pnpm -g

WORKDIR /app

COPY ./package.json /app

COPY ./pnpm-lock.yaml /app

RUN pnpm install

COPY . /app

# RUN pnpm run build

# service
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY --from=frontend /app/dist /app/public

# 安装 nginx
RUN apt-get update && apt-get install -y nginx

COPY ./docker-compose/server.conf /etc/nginx/conf.d
COPY ./docker-compose/nginx.conf /etc/nginx


EXPOSE 80

CMD service nginx start
