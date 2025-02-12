#!/bin/bash

# Build Docker images

echo "Building Docker images for ARM64..."

# Enable Minikube registry
minikube addons enable registry

# Build and tag images for Minikube registry
cd /Users/tspencer/Desktop/shopping-cluster/discovery-server
docker build -t discovery-server:local .
docker tag discovery-server:local localhost:60044/discovery-server:latest

cd /Users/tspencer/Desktop/shopping-cluster/api-gateway
docker build -t api-gateway:local .
docker tag api-gateway:local localhost:60044/api-gateway:latest

cd /Users/tspencer/Desktop/shopping-cluster/order-service
docker build -t order-service:local .
docker tag order-service:local localhost:60044/order-service:latest

cd /Users/tspencer/Desktop/shopping-cluster/product-service
docker build -t product-service:local .
docker tag product-service:local localhost:60044/product-service:latest

cd /Users/tspencer/Desktop/shopping-cluster/basket-service
docker build -t basket-service:local .
docker tag basket-service:local localhost:60044/basket-service:latest

# Build and push database images

# PostgreSQL
cd /Users/tspencer/Desktop/shopping-cluster/kubernetes/postgres
docker build -t postgres-database:local .
docker tag postgres-database:local localhost:60044/postgres-database:latest

# MongoDB
cd /Users/tspencer/Desktop/shopping-cluster/kubernetes/mongodb
docker build -t mongodb-database:local .
docker tag mongodb-database:local localhost:60044/mongodb-database:latest

# Deploy to Kubernetes
cd /Users/tspencer/Desktop/shopping-cluster/kubernetes
kubectl apply -f api-gateway/deployment.yaml
kubectl apply -f basket-service/deployment.yaml
kubectl apply -f discovery-server/deployment.yaml
kubectl apply -f order-service/deployment.yaml
kubectl apply -f product-service/deployment.yaml
kubectl apply -f frontend-service/deployment.yaml

# Add deployment for databases if applicable
kubectl apply -f postgres-database/deployment.yaml
kubectl apply -f mongodb-database/deployment.yaml

echo "Deployment complete!"
