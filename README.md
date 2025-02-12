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

## Debugging in Kubernetes

### Remote Debugging Setup

The application is configured for remote debugging in Kubernetes. Here's how it works:

1. Frontend Service (port 5005)
   - Starts immediately without waiting for debugger
   - Configured to handle basket service unavailability gracefully
   - Uses JSON for inter-service communication

2. Basket Service (port 5006)
   - Waits for debugger connection before processing requests
   - Configured in Kubernetes deployment:
   ```yaml
   command: ["java"]
   args: [
     "-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=*:5006",
     "-jar",
     "app.jar"
   ]
   ```

### VSCode Debug Configuration

Create `.vscode/launch.json`:
```json
{
    "version": "0.2.0",
    "configurations": [
        {
            "type": "java",
            "name": "Debug Frontend Service",
            "request": "attach",
            "hostName": "localhost",
            "port": 5005,
            "projectName": "frontend-service",
            "sourcePaths": [
                "${workspaceFolder}/frontend-service/src/main/java"
            ]
        },
        {
            "type": "java",
            "name": "Debug Basket Service",
            "request": "attach",
            "hostName": "localhost",
            "port": 5006,
            "projectName": "basket-service",
            "sourcePaths": [
                "${workspaceFolder}/basket-service/src/main/java"
            ],
            "timeout": 30000
        }
    ]
}
```

### Port Forwarding

Set up port forwarding to access debug ports:
```bash
kubectl port-forward deployment/frontend-service 5005:5005
kubectl port-forward deployment/basket-service 5006:5006
```

### Key Learnings

1. **Debug Configuration Location**: Place debug flags in Kubernetes deployment rather than Dockerfile for better control
2. **Inter-service Communication**: Use consistent media types (JSON) between services
3. **Error Handling**: Implement graceful degradation when dependent services are unavailable
4. **Container Configuration**:
   - Use `imagePullPolicy: Always` to ensure latest images
   - Expose debug ports in both deployment and service
5. **VSCode Setup**:
   - Configure separate debug ports for each service
   - Add adequate timeout for debugger connection
   - Match source paths correctly

### Troubleshooting

1. If services fail with 415 Unsupported Media Type:
   - Ensure consistent media type usage (JSON recommended)
   - Check Content-Type headers in requests
   - Verify @RequestBody annotations in controllers

2. If debugger fails to connect:
   - Verify port forwarding is active
   - Check debug ports are exposed in deployment
   - Ensure launch.json configuration matches deployment ports

## Frontend Application Preview

Here is a preview of what the frontend application looks like:

![Frontend Application](https://github.com/user-attachments/assets/d3d690a1-a998-497d-b5d5-1dd444de7cc7)

This image provides a visual representation of the user interface, showcasing the layout and design of the frontend application.

## Running in Kubernetes

### Ensuring Docker Images are Available to Minikube

When working with Minikube and local Docker images, it's crucial to ensure that your images are built within Minikube's Docker environment. This can be done using the following command:

1. **Switch to Minikube's Docker Environment:**
   - Run this command to configure your terminal to use Minikube's Docker daemon:
     ```bash
     eval $(minikube docker-env)
     ```
   - This ensures that any Docker images you build are available to the Minikube cluster.

2. **Build Docker Images:**
   - After switching to Minikube's Docker environment, build your Docker images as usual:
     ```bash
     docker build -t your-image-name:tag .
     ```

By following these steps, you can efficiently use Minikube for local development with Docker images, ensuring that updates to images are reflected in your deployments.

### Prerequisites

1. Kubernetes cluster (e.g., minikube, kind, or Docker Desktop)
2. kubectl configured to use your cluster
3. Local Docker registry running on port 5000
4. Java 17 and Maven installed

### Build and Deploy

1. Start local Docker registry (if not running):
```bash
docker run -d -p 5000:5000 --name registry registry:2
```

2. Build and push services:
```bash
# Build and push frontend service
cd frontend-service
mvn clean package -DskipTests
docker build -t localhost:5000/frontend-service .
docker push localhost:5000/frontend-service

# Build and push basket service
cd ../basket-service
mvn clean package -DskipTests
docker build -t localhost:5000/basket-service .
docker push localhost:5000/basket-service
```

3. Deploy infrastructure:
```bash
# Create namespace
kubectl create namespace shopping

# Deploy PostgreSQL
kubectl apply -f kubernetes/postgres/

# Deploy config and secrets
kubectl apply -f kubernetes/config/
```

4. Deploy services:
```bash
# Deploy basket service
kubectl apply -f kubernetes/basket-service/

# Deploy frontend service
kubectl apply -f kubernetes/frontend-service/
```

## Accessing the Frontend Application

To access the frontend application running in your Minikube cluster, you can use direct pod port forwarding. This method bypasses any service configuration issues and allows you to connect directly to the pod.

### Steps to Access the Application:

1. **Get Pod Name:**
   - Ensure you have the correct pod name by listing the pods:
     ```bash
     kubectl get pods
     ```

2. **Port Forward to Pod:**
   - Forward a local port to the pod's port 8085. For example, to use local port 8086:
     ```bash
     kubectl port-forward pod/<frontend-service-pod-name> 8086:8085
     ```

3. **Access the Application:**
   - Open your web browser and navigate to:
     ```
     http://127.0.0.1:8086/
     ```

This setup allows you to access the application directly, making it easier to debug and test without modifying service configurations.

### Access the Application

1. Get NodePort for frontend service:
```bash
kubectl get svc frontend-service
```

2. Access the application:
- If using minikube: `minikube service frontend-service --url`
- If using Docker Desktop: `http://localhost:<NodePort>`

### Monitoring and Logs

1. Check pod status:
```bash
kubectl get pods -l app=frontend-service
kubectl get pods -l app=basket-service
```

2. View logs:
```bash
kubectl logs -f -l app=frontend-service
kubectl logs -f -l app=basket-service
```

### Cleanup

To remove all resources:
```bash
kubectl delete -f kubernetes/frontend-service/
kubectl delete -f kubernetes/basket-service/
kubectl delete -f kubernetes/postgres/
kubectl delete -f kubernetes/config/
```

### Common Issues

1. **ImagePullBackOff**:
   - Ensure local registry is running
   - Check image names and tags
   - Verify images are pushed to registry

2. **CrashLoopBackOff**:
   - Check logs with `kubectl logs`
   - Verify config and secrets are deployed
   - Check PostgreSQL is running

3. **Service Unavailable**:
   - Verify pods are running (`kubectl get pods`)
   - Check service endpoints (`kubectl get endpoints`)
   - Ensure NodePorts are accessible
