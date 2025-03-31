-- Criação do banco de dados
CREATE DATABASE IF NOT EXISTS uniparking;
USE uniparking;

-- Tabela de Usuários
CREATE TABLE IF NOT EXISTS Usuario (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    login VARCHAR(50) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    nome VARCHAR(100),
    role VARCHAR(20)
);

-- Tabela de Clientes
CREATE TABLE IF NOT EXISTS Cliente (
    id_cliente INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    telefone VARCHAR(20),
    email VARCHAR(100),
    endereco VARCHAR(200),
    ativo BOOLEAN DEFAULT TRUE,
    id_usuario INT,
    FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario)
);

-- Tabela de Veículos
CREATE TABLE IF NOT EXISTS Veiculo (
    id INT AUTO_INCREMENT PRIMARY KEY,
    placa VARCHAR(10) NOT NULL,
    marca VARCHAR(50),
    id_cliente INT,
    FOREIGN KEY (id_cliente) REFERENCES Cliente(id_cliente)
);

-- Tabela de Estacionamentos
CREATE TABLE IF NOT EXISTS Estacionamento (
    id_estacionamento INT AUTO_INCREMENT PRIMARY KEY,
    qnt_vagas INT NOT NULL,
    nome VARCHAR(100) NOT NULL,
    endereco VARCHAR(200) NOT NULL,
    ativo BOOLEAN DEFAULT TRUE,
    id_usuario INT,
    FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario)
);

-- Tabela de Vagas
CREATE TABLE IF NOT EXISTS Vaga (
    id_vaga INT AUTO_INCREMENT PRIMARY KEY,
    numero_vaga VARCHAR(10) NOT NULL,
    id_estacionamento INT NOT NULL,
    FOREIGN KEY (id_estacionamento) REFERENCES Estacionamento(id_estacionamento)
);

-- Tabela de Registros (Entrada/Saída)
CREATE TABLE IF NOT EXISTS Registro (
    id_registro INT AUTO_INCREMENT PRIMARY KEY,
    data_entrada DATETIME NOT NULL,
    data_saida DATETIME,
    id_veiculo INT,
    id_vaga INT,
    valor_cobrado DECIMAL(10,2),
    duracao INT,
    FOREIGN KEY (id_veiculo) REFERENCES Veiculo(id),
    FOREIGN KEY (id_vaga) REFERENCES Vaga(id_vaga)
);

-- Tabela de Valores (Tarifas)
CREATE TABLE IF NOT EXISTS Valores (
    id INT AUTO_INCREMENT PRIMARY KEY,
    valor_hora DECIMAL(10,2) NOT NULL,
    tempo_minimo INT NOT NULL,
    id_estacionamento INT,
    FOREIGN KEY (id_estacionamento) REFERENCES Estacionamento(id_estacionamento)
);
