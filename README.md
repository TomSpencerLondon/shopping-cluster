# Shopping Microservices Cluster

This project implements a shopping system using Spring Cloud Microservices architecture. The system consists of the following components:

## Services
- **API Gateway**: Routes and manages requests to microservices
- **Basket Service**: Manages shopping cart operations
- **Product Service**: Handles product catalog and inventory
- **Order Service**: Processes and manages orders

## Technology Stack
- Java 17
- Spring Boot 3.2.1
- Spring Cloud 2023.0.0
- PostgreSQL
- Docker
- Kubernetes

## Project Structure
```
shopping-cluster/
├── api-gateway/
├── basket-service/
├── product-service/
├── order-service/
├── kubernetes/
├── pom.xml
└── README.md
```

## Building and Running
1. Build all services:
```bash
mvn clean package
```

2. Build Docker images:
```bash
docker-compose build
```

3. Deploy to Kubernetes:
```bash
kubectl apply -f kubernetes/
```

## Service Ports
- API Gateway: 8080
- Basket Service: 8081
- Product Service: 8082
- Order Service: 8083

## API Documentation
Each service has its own Swagger UI available at `/swagger-ui.html`
