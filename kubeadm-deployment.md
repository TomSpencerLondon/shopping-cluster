# 🚀 Deploying Spring Cloud Microservices on Kubernetes using `kubeadm`

This guide provides **step-by-step instructions** on how to deploy a **Spring Cloud microservices architecture** using **Kubeadm** on a Linux-based Kubernetes cluster.

---

## **📜 Prerequisites**
Before proceeding, ensure you have:
- **Linux Server(s)** (Ubuntu 22.04 or CentOS 8 recommended).
- **At least 2 vCPUs & 4GB RAM** (for master node).
- **Kubernetes installed via `kubeadm`**.
- **Docker installed** (for containerization).
- **kubectl installed** (for managing Kubernetes).
- **Helm installed** (for package management).
- **A network plugin (Calico or Flannel) installed**.

---

## **1️⃣ Install Kubernetes on Linux (Master Node)**
Run the following commands on the **Kubernetes master node**:

```sh
sudo apt update && sudo apt upgrade -y
sudo apt install -y curl apt-transport-https
curl -s https://packages.cloud.google.com/apt/doc/apt-key.gpg | sudo apt-key add -

# Add Kubernetes repository
echo "deb https://apt.kubernetes.io/ kubernetes-xenial main" | sudo tee /etc/apt/sources.list.d/kubernetes.list
sudo apt update

# Install kubeadm, kubelet, and kubectl
sudo apt install -y kubeadm kubelet kubectl
```

**Disable swap (required by Kubernetes):**
```sh
sudo swapoff -a
sudo sed -i '/ swap / s/^/#/' /etc/fstab
```

---

## **2️⃣ Initialize the Kubernetes Cluster**
On the master node, run:

```sh
sudo kubeadm init --pod-network-cidr=192.168.0.0/16
```

When complete, **copy and save the `kubeadm join` command** for worker nodes.

Set up Kubernetes for the current user:
```sh
mkdir -p $HOME/.kube
sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
sudo chown $(id -u):$(id -g) $HOME/.kube/config
```

---

## **3️⃣ Install a Network Plugin (Calico)**
To enable pod-to-pod communication, install **Calico**:

```sh
kubectl apply -f https://docs.projectcalico.org/manifests/calico.yaml
```

Check the status:
```sh
kubectl get pods -n kube-system
```

---

## **4️⃣ Join Worker Nodes to the Cluster**
Run the `kubeadm join` command from **Step 2** on each worker node:
```sh
sudo kubeadm join <master-ip>:6443 --token <token> --discovery-token-ca-cert-hash sha256:<hash>
```

Verify that all nodes joined the cluster:
```sh
kubectl get nodes
```

---

## **5️⃣ Deploy PostgreSQL Database**
Create a **PostgreSQL deployment** for the basket and order services:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: postgres
spec:
  replicas: 1
  selector:
    matchLabels:
      app: postgres
  template:
    metadata:
      labels:
        app: postgres
    spec:
      containers:
        - name: postgres
          image: postgres:14
          env:
            - name: POSTGRES_USER
              value: "user"
            - name: POSTGRES_PASSWORD
              value: "password"
            - name: POSTGRES_DB
              value: "basketdb"
          ports:
            - containerPort: 5432
```

Apply the deployment:
```sh
kubectl apply -f postgres-deployment.yaml
```

Expose the database:
```yaml
apiVersion: v1
kind: Service
metadata:
  name: postgres-service
spec:
  selector:
    app: postgres
  ports:
    - protocol: TCP
      port: 5432
      targetPort: 5432
  type: ClusterIP
```

Apply the service:
```sh
kubectl apply -f postgres-service.yaml
```

---

## **6️⃣ Deploy Spring Cloud Microservices**
Each microservice (Basket, Product, Order) has its own deployment file.  
Example **Basket Service** deployment:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: basket-service
spec:
  replicas: 2
  selector:
    matchLabels:
      app: basket-service
  template:
    metadata:
      labels:
        app: basket-service
    spec:
      containers:
        - name: basket-service
          image: basket-service:latest
          ports:
            - containerPort: 8080
          env:
            - name: DATABASE_URL
              value: "jdbc:postgresql://postgres-service:5432/basketdb"
```

Apply the deployment:
```sh
kubectl apply -f basket-service-deployment.yaml
```

Repeat this for **product-service** and **order-service**.

---

## **7️⃣ Deploy API Gateway**
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: api-gateway
spec:
  replicas: 1
  selector:
    matchLabels:
      app: api-gateway
  template:
    metadata:
      labels:
        app: api-gateway
    spec:
      containers:
        - name: api-gateway
          image: api-gateway:latest
          ports:
            - containerPort: 8080
```

Expose the API Gateway:
```yaml
apiVersion: v1
kind: Service
metadata:
  name: api-gateway
spec:
  selector:
    app: api-gateway
  ports:
    - protocol: TCP
      port: 80
      targetPort: 8080
  type: LoadBalancer
```

Apply the service:
```sh
kubectl apply -f api-gateway.yaml
```

---

## **8️⃣ Verify Deployment**
Check that all services are running:
```sh
kubectl get pods
```

Get the external API Gateway URL:
```sh
kubectl get svc api-gateway
```

Test the application:
```sh
curl http://<API-GATEWAY-IP>/api/basket/1
```

---

## **9️⃣ Enable Helm for Configuration Management**
Helm helps manage deployments with templates.

1. Install Helm:
```sh
curl https://raw.githubusercontent.com/helm/helm/master/scripts/get-helm-3 | bash
```

2. Create a Helm chart:
```sh
helm create spring-microservices
```

3. Deploy using Helm:
```sh
helm install my-microservices spring-microservices
```

---

## **🔹 Final Kubernetes Architecture**
```
                     ┌──────────────────────┐
                     │       Client         │
                     └────────▲─────────────┘
                              │
                     ┌────────▼──────────┐
                     │  Spring Cloud API │
                     │     Gateway       │
                     └────────▲──────────┘
                              │
          ┌───────────────────┴───────────────────┐
          │                   │                   │
 ┌────────▼────────┐  ┌────────▼────────┐  ┌────────▼────────┐
 │ Basket Service  │  │ Product Service │  │ Order Service   │
 └────────▲────────┘  └────────▲────────┘  └────────▲────────┘
          │                   │                   │
 ┌────────▼────────┐  ┌────────▼────────┐  ┌────────▼────────┐
 │  PostgreSQL DB  │  │  Kubernetes DNS │  │  External APIs  │
 └─────────────────┘  └─────────────────┘  └─────────────────┘
```

---

# 🎯 **Next Steps**
- ✅ **Ensure all services are running** (`kubectl get pods`).
- ✅ **Check logs for errors** (`kubectl logs -l app=basket-service`).

🚀 **This guide sets up a full Spring Cloud microservices architecture on a Kubernetes cluster using `kubeadm`!** 😊
