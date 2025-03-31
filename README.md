# 🅿️ UniParking

UniParking é uma aplicação Java Spring Boot desenvolvida como projeto acadêmico para gerenciamento de estacionamentos universitários. O sistema oferece uma solução completa para controle de vagas, veículos, clientes e pagamentos.

<!-- TOC -->
* [✨ Funcionalidades Implementadas](#-funcionalidades-implementadas)
* [🏗️ Arquitetura](#️-arquitetura)
* [🚀 Instalação](#-instalação)
* [🛠️ Tecnologias Utilizadas](#️-tecnologias-utilizadas)
* [📝 Modelos de Dados](#-modelos-de-dados)
* [👨‍💻 Autores](#-autores)
* [📄 Licença](#-licença)
* [🖼️ Imagens do Sistema](#️-imagens-do-sistema)
* [💾 Banco de Dados](#-banco-de-dados)
<!-- TOC -->

## ✨ [Funcionalidades Implementadas](#-funcionalidades-implementadas)

* **🔐 Autenticação**: Sistema de login seguro.
* **🚗 Gestão de Veículos**: Cadastro e controle de veículos com informações de placa e marca
* **👥 Gestão de Clientes**: Cadastro completo de clientes com informações de contato
* **🏢 Gestão de Estacionamentos**: Cadastro e controle de múltiplos estacionamentos
* **🅿️ Controle de Vagas**: Gerenciamento de vagas disponíveis por estacionamento
* **⏱️ Registro de Entrada/Saída**: Controle preciso do tempo de permanência dos veículos
* **💰 Cálculo Automático de Tarifas**: Baseado no tempo de permanência e valores configuráveis
* **📊 Relatórios em PDF**: Geração de relatórios detalhados sobre ocupação e faturamento
* **📱 Interface Responsiva**: Design adaptável para diferentes dispositivos

## 🏗️ [Arquitetura](#️-arquitetura)

O projeto segue uma arquitetura em camadas:

* **🖥️ Camada de Apresentação**: Templates Thymeleaf para interface web
* **⚙️ Camada de Negócios**: Serviços Spring para lógica de negócios
* **💾 Camada de Acesso a Dados**: Spring Data JPA para operações de banco de dados
* **🔒 Camada de Segurança**: Spring Security para autenticação e autorização

## 🚀 [Instalação](#-instalação)

1. Clone este repositório
2. Importe como projeto Maven na sua IDE
3. Configure a conexão com o banco de dados no arquivo `application.properties`
4. Execute usando `mvn spring-boot:run`

## 🛠️ [Tecnologias Utilizadas](#️-tecnologias-utilizadas)

* Java 23
* Spring Boot 3.4.3
* Spring Data JPA
* Spring Security
* Thymeleaf
* MySQL (produção)
* H2 Database (desenvolvimento)
* iTextPDF 5.5.13
* Maven

## 📝 [Modelos de Dados](#-modelos-de-dados)

O sistema utiliza os seguintes modelos principais:

* **Usuario**: Gerenciamento de usuários do sistema
* **Cliente**: Cadastro de clientes que utilizam o estacionamento
* **Veiculo**: Registro de veículos associados aos clientes
* **Estacionamento**: Configuração dos estacionamentos disponíveis
* **Vaga**: Controle das vagas em cada estacionamento
* **Registro**: Histórico de entradas e saídas de veículos
* **Valores**: Configuração de tarifas e tempos mínimos

## 👨‍💻 [Autores](#-autores)

* Giovanne Monti Guedes (G763289)
* Isabela Cicilio de Andrade (G8694J8)
* Raphael Della Torre Gimenes (N202HJ4)
* Caio Pacheco Andrade (N089695)

## 📄 [Licença](#-licença)

Este projeto está licenciado sob a [Licença MIT](LICENSE).

## 🖼️ [Imagens do Sistema](#️-imagens-do-sistema)

Abaixo estão algumas imagens representativas do sistema:

### Login
<img src="https://i.imgur.com/TSkBPFv.jpeg" alt="Login" width="640" height="480" style="max-width: 100%; height: auto;">

### Index
<img src="https://i.imgur.com/dPKDV8u.jpeg" alt="Index" width="640" height="480" style="max-width: 100%; height: auto;">

### Registros
<img src="https://i.imgur.com/m3NKpiE.jpeg" alt="Registros" width="640" height="480" style="max-width: 100%; height: auto;">

## 💾 [Banco de Dados](#-banco-de-dados)

O arquivo `src/main/resources/schema.sql` contém todos os comandos SQL necessários para criar o banco de dados completo, incluindo todas as tabelas com seus relacionamentos.
