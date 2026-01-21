# Desafio Técnico: API de Gerenciamento de Cupons

Olá! Meu nome é Hugo Franco e estou muito animado com a oportunidade de me candidatar à vaga de Desenvolvedor Pleno. Este projeto é a minha resposta ao desafio técnico proposto, focado na implementação de uma API RESTful para gerenciamento de cupons, seguindo as regras de negócio e requisitos técnicos fornecidos.

A aplicação foi desenvolvida utilizando **Java 21** e **Spring Boot 3.2**, priorizando a clareza do código, a robustez das regras de negócio e a cobertura de testes.

## 🚀 Tecnologias Utilizadas

| Categoria | Tecnologia | Versão |
| :--- | :--- | :--- |
| Linguagem | Java | 21 (LTS) |
| Framework | Spring Boot | 3.2.1 |
| Persistência | Spring Data JPA / Hibernate | Integrado |
| Banco de Dados | H2 Database | Em memória (para o desafio) |
| Build Tool | Apache Maven | 3.9.6 |
| Documentação | Springdoc OpenAPI | 2.3.0 |
| Testes | JUnit 5 / Mockito | Integrado |

## 🛠️ Como Rodar o Projeto

### Pré-requisitos

Certifique-se de ter instalado em sua máquina:
*   **Java Development Kit (JDK)**: Versão 21 ou superior.
*   **Apache Maven**: Versão 3.6 ou superior.
*   **Docker e Docker Compose** (opcional, mas recomendado).

### Opção 1: Execução Local com Maven

1.  Navegue até o diretório raiz do projeto (`coupon-manager`).
2.  Compile e execute a aplicação:
    ```bash
    mvn clean install
    mvn spring-boot:run
    ```
3.  A API estará disponível em `http://localhost:8080`.

### Opção 2: Execução com Docker Compose (Recomendado)

Esta é a forma mais rápida e limpa de rodar o projeto, pois encapsula o ambiente.

1.  Navegue até o diretório raiz do projeto (`coupon-manager`).
2.  Construa a imagem e inicie o container:
    ```bash
    mvn clean package -DskipTests # Cria o JAR
    docker-compose up --build
    ```
3.  A API estará disponível em `http://localhost:8080`.

## 🧪 Validação do Funcionamento

A validação do funcionamento e a garantia de que as regras de negócio foram atendidas foram realizadas através de **testes automatizados**, atingindo **100% de cobertura** nas classes de domínio e serviço, superando o requisito de 80%.

1.  **Execução dos Testes:**
    ```bash
    mvn test
    ```
2.  **Tipos de Testes Implementados:**
    *   **Testes Unitários (`CouponTest`, `CouponServiceTest`):** Focados na validação das regras de negócio encapsuladas na entidade `Coupon` (limpeza de código, valor mínimo de desconto, data de expiração) e na lógica do serviço (soft delete).
    *   **Testes de Integração (`CouponControllerIntegrationTest`):** Utilizando `MockMvc` para simular requisições HTTP e validar o fluxo completo da API, incluindo a correta manipulação de exceções de regra de negócio (e.g., tentar deletar um cupom já deletado).
3.  **Documentação Interativa (Swagger UI):**
    Após iniciar a aplicação, a documentação interativa da API está disponível em:
    `http://localhost:8080/swagger-ui.html`
    Isso permite testar manualmente os endpoints `POST /coupon` e `DELETE /coupon/{id}`.

## 💡 Principais Decisões Técnicas

Minhas escolhas arquiteturais foram guiadas pelo princípio de **separação de responsabilidades** e **encapsulamento das regras de negócio**, alinhadas com os conceitos de **Clean Architecture** e **Domain-Driven Design (DDD) Lite**.

| Decisão Técnica | Justificativa |
| :--- | :--- |
| **Encapsulamento no Domínio** | As regras de negócio mais críticas (validação de código, data e valor) foram implementadas diretamente na entidade `Coupon` (método `prePersist`). Isso garante que a entidade seja sempre consistente, independentemente de onde for salva. |
| **Soft Delete** | O requisito de "soft delete" foi implementado com o campo `status` (`ACTIVE`, `INACTIVE`, `DELETED`), preservando o histórico de dados e atendendo à regra de não permitir a exclusão de um cupom já deletado. |
| **Uso de DTOs** | Separação clara entre o modelo de domínio (`Coupon`) e o modelo de comunicação (`CouponRequest`, `CouponResponse`), protegendo a entidade de manipulações externas e facilitando a validação de entrada (`@Valid`). |
| **Tratamento Global de Exceções** | Utilização de `@ControllerAdvice` e uma exceção customizada (`BusinessRuleException`) para padronizar as respostas de erro da API, retornando `HTTP 400 Bad Request` com mensagens claras para o cliente. |
| **Java 21** | Uso de recursos modernos da linguagem e do ecossistema Spring Boot 3, garantindo um código mais conciso e performático. |

## ⏭️ O que Faria Diferente com Mais Tempo

Com mais tempo disponível, as seguintes melhorias seriam implementadas para elevar a qualidade e a prontidão para produção do projeto:

1.  **Persistência Real:** Substituir o banco de dados H2 em memória por uma solução robusta como **PostgreSQL**, configurando um container dedicado no `docker-compose.yml` para simular um ambiente de produção.
2.  **Mapeamento de DTOs (MapStruct):** Implementar uma biblioteca de mapeamento (e.g., MapStruct) para remover o código boilerplate de conversão entre DTOs e Entidades, tornando o `CouponService` mais limpo e focado na lógica de negócio.
3.  **Value Objects para o Código:** Criar um `Value Object` dedicado para o `CouponCode`. Isso encapsularia a lógica de limpeza e validação do código de forma mais isolada e reutilizável, aderindo ao princípio de **Tell, Don't Ask**.
4.  **Implementação Completa da API:** Adicionar os endpoints de consulta (`GET /coupon/{id}` e `GET /coupon`) para completar o ciclo de vida do recurso.
5.  **CI/CD Pipeline:** Configurar um pipeline básico (e.g., GitHub Actions) para automatizar a compilação, execução de testes e a construção da imagem Docker a cada *push*.

Agradeço a oportunidade e estou à disposição para discutir este projeto em mais detalhes.
