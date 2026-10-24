# StockFlow PDV

Sistema completo de gestão comercial desenvolvido para pequenas e médias empresas, com controle de estoque, vendas, clientes, fiado, promoções inteligentes e auditoria de operações.

## 🚀 Principais Recursos

✅ Multi-tenant  
✅ Autenticação JWT + Refresh Token  
✅ Controle de acesso baseado em perfis  
✅ Gestão de Estoque  
✅ Gestão de Produtos  
✅ Gestão de Clientes  
✅ Gestão de Vendas  
✅ Controle de Fiados  
✅ Gestão de Pagamentos  
✅ Promoções Inteligentes  
✅ Auditoria Completa de Operações  
✅ API REST Documentada com Swagger/OpenAPI

---

# 📋 Funcionalidades

## 🔐 Autenticação e Segurança

- Login com JWT
- Refresh Token
- Controle de acesso por perfil
- Rotas protegidas com Spring Security
- Multi-tenant para isolamento de dados

### Perfis disponíveis

- SUPER_ADMIN
- PROPRIETARIO
- SOCIO
- GERENTE
- OPERADOR_CAIXA
- ESTOQUISTA

---

## 👥 Gestão de Usuários

- Cadastro de usuários
- Edição de usuários
- Inativação de usuários
- Alteração de senha
- Controle de permissões por perfil

---

## 📦 Gestão de Produtos

- Cadastro de produtos
- Atualização de produtos
- Inativação e reativação
- Controle de estoque
- Controle de custo médio
- Vinculação com promoções

---

## 🛒 Gestão de Vendas

- Venda à vista
- Venda fiado
- Cancelamento de venda
- Baixa automática de estoque

---

## 👤 Gestão de Clientes

- Cadastro de clientes
- Atualização de dados
- Inativação e reativação
- Limite de crédito
- Resumo financeiro
- Controle de saldo devedor

---

## 💰 Controle de Fiados

- Lançamento de fiados
- Consulta de débitos
- Controle de crédito disponível
- Histórico financeiro por cliente

---

## 💳 Pagamentos

- Registro de pagamentos
- Abatimento automático do saldo devedor
- Histórico de pagamentos

---

## 🎯 Promoções Inteligentes

- Identificação de produtos com baixo giro
- Sugestão automática de promoções
- Controle de metas de venda
- Encerramento manual de promoções
- Encerramento automático por meta atingida

---

## 📊 Movimentação de Estoque

- Entrada de estoque
- Saída de estoque
- Ajuste manual de estoque
- Atualização automática do custo médio

---

# 📝 Auditoria de Operações

O sistema registra automaticamente as principais operações realizadas pelos usuários.

### Produtos

- PRODUTO_CRIADO
- PRODUTO_EDITADO
- PRODUTO_INATIVADO
- PRODUTO_REATIVADO

### Clientes

- CLIENTE_CRIADO
- CLIENTE_EDITADO
- CLIENTE_INATIVADO
- CLIENTE_REATIVADO

### Usuários

- USUARIO_CRIADO
- USUARIO_EDITADO
- USUARIO_INATIVADO
- SENHA_ALTERADA

### Vendas

- VENDA_CRIADA
- VENDA_CANCELADA

### Financeiro

- FIADO_CRIADO
- PAGAMENTO_REALIZADO

### Promoções

- PROMOCAO_CRIADA
- PROMOCAO_ENCERRADA
- PROMOCAO_FINALIZADA_META

### Estoque

- ENTRADA_ESTOQUE
- SAIDA_ESTOQUE
- AJUSTE_ESTOQUE

---

# 🏗️ Arquitetura

O backend segue arquitetura em camadas:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
Database
```

## Estrutura de Pacotes

```text
src/main/java/br/com/stockflow/stockflow_api

├── config
├── controller
├── dto
├── entity
├── exception
├── repository
├── security
└── service
```

---

# 🛠️ Tecnologias Utilizadas

## Backend

- Java 21
- Spring Boot
- Spring Security
- Spring Data JPA
- Hibernate
- JWT
- PostgreSQL
- Maven

## Frontend

- Angular
- TypeScript
- Angular Material
- RxJS

---

# ⚙️ Executando o Projeto

## Pré-requisitos

- Java 21+
- Maven 3.9+
- PostgreSQL
- Node.js
- Angular CLI
- Docker

---
# 🐳 Docker
O projeto pode ser executado em containers Docker.

Gerar o artefato
```
./mvnw clean package
```
Construir a imagem Shell
```
docker build -t stockflow-api .
```

Executar diretamente Shell
```
docker run -p 8080:8080 stockflow-api

docker compose up -d
```
Aplicação disponível em: 
```
http://localhost:8080
```

## Clonar o Repositório

```bash
git clone https://github.com/seu-usuario/stockflow-pdv-api.git
```

```bash
cd stockflow-pdv-api
```

---

## Configurar Banco de Dados

Altere o arquivo:

```properties
application.properties
```

Exemplo:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/stockflow
spring.datasource.username=postgres
spring.datasource.password=senha
```

---

## Executar Backend

```bash
mvn clean install
```

```bash
mvn spring-boot:run
```

Aplicação disponível em:

```text
http://localhost:8080
```

---

# 📚 Documentação da API

Após iniciar o backend:

```text
http://localhost:8080/swagger-ui/index.html
```

A documentação é gerada automaticamente via OpenAPI/Swagger.

---

# 🔒 Segurança

O sistema utiliza autenticação baseada em JWT.

Fluxo:

```text
Login
   ↓
JWT Access Token
   ↓
Consumo dos Endpoints
   ↓
Refresh Token
   ↓
Novo Access Token
```

---

# 📈 Próximas Evoluções

- Dashboard gerencial
- Relatórios PDF
- Exportação para Excel
- Indicadores financeiros
- Métricas de vendas
- Notificações automáticas
- Deploy em nuvem

---

# 👩‍💻 Autoria

**Aline Melissa Andrade de Almeida**

Assistente de Engenharia de Software

Projeto desenvolvido para estudo, prática de arquitetura de sistemas, APIs REST, segurança com JWT, gestão empresarial e boas práticas com Spring Boot e 
