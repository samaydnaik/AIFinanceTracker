CREATE TABLE income_sources (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    amount DECIMAL(12,2),
    frequency VARCHAR(20),
    start_date DATE
);

CREATE TABLE expenditures (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    amount DECIMAL(12,2) NOT NULL,
    category VARCHAR(50),
    date DATE,
    merchant VARCHAR(100),
    recurring BOOLEAN DEFAULT FALSE
);

CREATE TABLE savings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    amount DECIMAL(12,2) NOT NULL,
    type VARCHAR(50),
    date DATE,
    maturity_date DATE,
    interest_rate DECIMAL(5,2)
);
