# 🚀 SmartLead – AI-Powered CRM SaaS Platform

> A production-ready, multi-tenant CRM system with AI-powered lead scoring designed for modern businesses.

## 🎯 Project Overview

SmartLead is a comprehensive CRM platform that helps businesses manage their sales pipeline efficiently. Built with modern technologies and cloud-native architecture, it provides AI-powered lead scoring, comprehensive analytics, and secure multi-tenant data management.

### Key Features

- 🧠 **AI Lead Scoring**: Intelligent lead ranking using OpenAI integration
- 👥 **Multi-Tenant SaaS**: Complete data isolation between organizations
- 📊 **Real-time Analytics**: Interactive dashboards and reporting
- 🔐 **Enterprise Security**: JWT + OAuth2 authentication with role-based access
- 📱 **Responsive Design**: Modern UI with Tailwind CSS
- ☁️ **Cloud-Native**: AWS deployment with Kubernetes orchestration
- 🔄 **CI/CD Pipeline**: Automated testing and deployment

## 🏗️ Architecture

### Tech Stack

| Layer | Technologies |
|-------|-------------|
| **Frontend** | React.js, TypeScript, Tailwind CSS |
| **Backend** | Java, Spring Boot 3, REST APIs |
| **Database** | PostgreSQL, Redis (caching) |
| **Authentication** | JWT, OAuth2, Spring Security |
| **Cloud** | AWS (EC2, RDS, S3, IAM, CloudWatch) |
| **DevOps** | Docker, Kubernetes (EKS), Terraform |
| **CI/CD** | GitHub Actions |
| **AI/ML** | OpenAI API, LangChain |
| **Monitoring** | Prometheus, Grafana, ELK Stack |

### System Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   React App     │    │  Spring Boot    │    │   PostgreSQL    │
│   (Frontend)    │◄──►│   (Backend)     │◄──►│   (Database)    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         │                       ▼                       │
         │              ┌─────────────────┐              │
         │              │     Redis       │              │
         │              │   (Caching)     │              │
         │              └─────────────────┘              │
         │                                                │
         ▼                                                ▼
┌─────────────────┐              ┌─────────────────┐
│   AWS S3        │              │   OpenAI API    │
│ (File Storage)  │              │ (AI Scoring)    │
└─────────────────┘              └─────────────────┘
```

## 🚀 Getting Started

### Prerequisites

- **Java 17+**
- **Node.js 18+**
- **Docker & Docker Compose**
- **PostgreSQL 14+**
- **Redis 6+**
- **AWS CLI** (for cloud deployment)
- **Terraform** (for infrastructure)

### Local Development Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/Sreekar50/smartlead-crm.git
   cd smartlead-crm
   ```

2. **Start services with Docker Compose**
   ```bash
   docker-compose up -d
   ```

3. **Backend Setup**
   ```bash
   cd backend/springboot-app
   ./mvnw spring-boot:run
   ```

4. **Frontend Setup**
   ```bash
   cd frontend/react-app
   npm install
   npm start
   ```

5. **Access the application**
   - Frontend: http://localhost:3000
   - Backend API: http://localhost:8080
   - API Documentation: http://localhost:8080/swagger-ui.html

### Environment Variables

Create `.env` files in both frontend and backend directories:

**Backend (.env)**
```env
DATABASE_URL=jdbc:postgresql://localhost:5432/smartlead
DATABASE_USERNAME=smartlead_user
DATABASE_PASSWORD=your_password
REDIS_URL=redis://localhost:6379
JWT_SECRET=your_jwt_secret
OPENAI_API_KEY=your_openai_key
AWS_ACCESS_KEY_ID=your_aws_key
AWS_SECRET_ACCESS_KEY=your_aws_secret
```

**Frontend (.env)**
```env
REACT_APP_API_URL=http://localhost:8080
REACT_APP_ENVIRONMENT=development
```

## 📁 Project Structure

```
smartlead-crm/
├── backend/
│   └── springboot-app/
│       ├── src/main/java/com/smartlead/
│       │   ├── SmartLeadApplication.java
│       │   ├── config/              # Configuration classes
│       │   ├── controller/          # REST controllers
│       │   ├── dto/                 # Data transfer objects
│       │   ├── entity/              # JPA entities
│       │   ├── repository/          # Data repositories
│       │   ├── service/             # Business logic
│       │   └── security/            # Security configuration
│       ├── src/main/resources/
│       ├── pom.xml
│       └── Dockerfile
├── frontend/
│   └── react-app/
│       ├── src/
│       │   ├── components/          # Reusable components
│       │   ├── pages/               # Page components
│       │   ├── services/            # API services
│       │   ├── contexts/            # React contexts
│       │   ├── utils/               # Utility functions
│       │   └── App.tsx
│       ├── package.json
│       ├── tailwind.config.js
│       └── Dockerfile
├── infrastructure/
│   └── terraform/                   # Infrastructure as Code
│       ├── main.tf
│       ├── variables.tf
│       └── outputs.tf
├── .github/workflows/
│   └── ci-cd.yml                    # GitHub Actions
├── docker-compose.yml
└── README.md
```

## 🔧 Development

### Running Tests

**Backend Tests**
```bash
cd backend/springboot-app
./mvnw test
```

**Frontend Tests**
```bash
cd frontend/react-app
npm test
```

**End-to-End Tests**
```bash
npm run test:e2e
```

### Code Quality

**Backend Code Style**
```bash
./mvnw spotless:apply
```

**Frontend Code Style**
```bash
npm run lint
npm run format
```

## 🌟 Core Features

### 1. User Management & Authentication
- Multi-tenant user registration and login
- Role-based access control (Admin, Sales Rep, Manager)
- OAuth2 integration with Google/Microsoft
- JWT token-based authentication

### 2. AI-Powered Lead Scoring
- Automatic lead classification (Hot, Warm, Cold)
- OpenAI integration for intelligent scoring
- Dynamic score updates based on interactions
- Customizable scoring criteria

### 3. Lead Management
- Lead import from CSV/Excel files
- Complete lead lifecycle tracking
- Interaction history and notes
- File attachments and document management

### 4. Analytics & Reporting
- Real-time dashboard with key metrics
- Lead conversion tracking
- Sales pipeline visualization
- Performance analytics by sales rep

### 5. Communication Tracking
- Email integration and logging
- Call history and notes
- Meeting scheduling and reminders
- Activity timeline

## 🚀 Deployment

### Local Docker Deployment

```bash
# Build and run all services
docker-compose up --build

# Scale services
docker-compose up --scale backend=3
```

### AWS Cloud Deployment

1. **Initialize Terraform**
   ```bash
   cd infrastructure/terraform
   terraform init
   terraform plan
   terraform apply
   ```

2. **Deploy to Kubernetes**
   ```bash
   # Configure kubectl
   aws eks update-kubeconfig --region us-west-2 --name smartlead-cluster

   # Deploy application
   kubectl apply -f k8s/
   ```

3. **GitHub Actions CI/CD**
   - Push to `main` branch triggers automatic deployment
   - Includes automated testing, building, and deployment
   - Blue-green deployment strategy

### Environment-Specific Configuration

- **Development**: Local Docker containers
- **Staging**: AWS EKS with reduced resources
- **Production**: AWS EKS with auto-scaling and monitoring

## 📊 API Documentation

### Authentication Endpoints
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration
- `POST /api/auth/refresh` - Token refresh

### Lead Management Endpoints
- `GET /api/leads` - Get all leads
- `POST /api/leads` - Create new lead
- `PUT /api/leads/{id}` - Update lead
- `DELETE /api/leads/{id}` - Delete lead
- `POST /api/leads/{id}/score` - Update AI score

### Dashboard Endpoints
- `GET /api/dashboard/metrics` - Get dashboard metrics
- `GET /api/dashboard/pipeline` - Get sales pipeline data
- `GET /api/dashboard/conversion` - Get conversion rates

For complete API documentation, visit: http://localhost:8080/swagger-ui.html

## 🛡️ Security Features

- **Multi-tenant Data Isolation**: Complete separation of tenant data
- **JWT Authentication**: Secure token-based authentication
- **Role-Based Access Control**: Fine-grained permission system
- **SQL Injection Prevention**: Parameterized queries and JPA
- **XSS Protection**: Content Security Policy and input sanitization
- **HTTPS Enforcement**: SSL/TLS encryption for all communications

## 🔍 Monitoring & Observability

### Metrics & Monitoring
- **Application Metrics**: Custom business metrics
- **System Metrics**: CPU, Memory, Disk usage
- **Database Metrics**: Connection pool, query performance
- **API Metrics**: Request rates, response times, error rates

### Logging
- **Structured Logging**: JSON format with correlation IDs
- **Log Aggregation**: ELK Stack (Elasticsearch, Logstash, Kibana)
- **Log Levels**: Configurable logging levels per environment

### Health Checks
- **Application Health**: Spring Boot Actuator endpoints
- **Database Health**: Connection and query health checks
- **External Service Health**: OpenAI API, AWS services


---

**Built with ❤️ by Sreekar**

> This project demonstrates production-ready development practices including microservices architecture, cloud-native deployment, AI integration, and modern DevOps practices.
