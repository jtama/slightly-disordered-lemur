#!/bin/zsh
./mvnw clean package;
docker build -f src/main/docker/Dockerfile.jvm -t xl642z35.gra7.container-registry.ovh.net/pangee/slightly-disordered-lemur:1.0 .
docker push xl642z35.gra7.container-registry.ovh.net/pangee/slightly-disordered-lemur:1.0
k apply -f kubernetes.yml -n lemure-dev
k apply -f target/kubernetes/randomkillrequests.yap.onepoint.com-v1.yml
k delete -f test.yaml -n lemure-dev
