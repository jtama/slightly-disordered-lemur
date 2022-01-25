#!/bin/zsh
./mvnw clean package -f operator/pom.xml;
docker build -f operator/src/main/docker/Dockerfile.jvm -t quay.io/jtama/slightly-disordered-lemur:1.0 operator
docker push quay.io/jtama/slightly-disordered-lemur:1.0
./mvnw clean package -f processor/pom.xml;
docker build -f processor/src/main/docker/Dockerfile.jvm -t quay.io/jtama/slightly-disordered-worker:1.0 processor
docker push quay.io/jtama/slightly-disordered-worker:1.0
kubectl apply -f operator/target/kubernetes/randomkillrequests.yap.onepoint.com-v1.yml
kubectl apply -f operator/target/kubernetes/randominvasionrequests.yap.onepoint.com-v1.yml
kubectl apply -f kubernetes.yml
