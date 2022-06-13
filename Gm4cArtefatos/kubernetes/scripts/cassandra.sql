create keyspace itau WITH replication = {'class':'SimpleStrategy', 'replication_factor' : 1};

use itau;

CREATE TABLE tef (id_tef uuid PRIMARY KEY, evento text, tipo text, agencia_origem int, conta_origem int, dv_origem int, agencia_destino int, conta_destino int, dv_destino int, timestamp timestamp, valor decimal, senha text, transactionId text, rc_simulacao text,msg_simulacao text, rc_senha text, msg_senha text, rc_limite text, msg_limite text, rc_credito text, msg_credito text, rc_debito text, msg_debito text, rc_efetivacao text, msg_efetivacao text);

CREATE TABLE senha (id_senha uuid PRIMARY KEY, agencia int, conta int, dv int, senha text);

CREATE TABLE limite(id_limite uuid PRIMARY KEY, agencia int, conta int, dv int, valor_limite decimal, valor_utilizado decimal, timestamp_limite text);

CREATE TABLE conta (id_conta uuid PRIMARY KEY, agencia int, conta int, dv int, valor_saldo decimal, bloqueio int);

INSERT INTO senha(id_senha, agencia, conta, dv, senha) VALUES (uuid(), 10, 10, 0, '123456');

INSERT INTO senha(id_senha, agencia, conta, dv, senha) VALUES (uuid(), 10, 11, 0, '123456');

INSERT INTO senha(id_senha, agencia, conta, dv, senha) VALUES (uuid(), 10, 12, 0, '123456');

INSERT INTO senha(id_senha, agencia, conta, dv, senha) VALUES (uuid(), 10, 13, 0, '123456');

INSERT INTO senha(id_senha, agencia, conta, dv, senha) VALUES (uuid(), 10, 14, 0, '123456');

INSERT INTO senha(id_senha, agencia, conta, dv, senha) VALUES (uuid(), 10, 15, 0, '123456');

INSERT INTO senha(id_senha, agencia, conta, dv, senha) VALUES (uuid(), 10, 16, 0, '123456');

INSERT INTO senha(id_senha, agencia, conta, dv, senha) VALUES (uuid(), 10, 17, 0, '123456');

INSERT INTO senha(id_senha, agencia, conta, dv, senha) VALUES (uuid(), 10, 18, 0, '123456');

INSERT INTO senha(id_senha, agencia, conta, dv, senha) VALUES (uuid(), 10, 19, 0, '123456');

INSERT INTO senha(id_senha, agencia, conta, dv, senha) VALUES (uuid(), 20, 20, 0, '123456');

INSERT INTO senha(id_senha, agencia, conta, dv, senha) VALUES (uuid(), 20, 21, 0, '123456');

INSERT INTO senha(id_senha, agencia, conta, dv, senha) VALUES (uuid(), 20, 22, 0, '123456');

INSERT INTO senha(id_senha, agencia, conta, dv, senha) VALUES (uuid(), 20, 23, 0, '123456');

INSERT INTO senha(id_senha, agencia, conta, dv, senha) VALUES (uuid(), 20, 24, 0, '123456');

INSERT INTO senha(id_senha, agencia, conta, dv, senha) VALUES (uuid(), 20, 25, 0, '123456');

INSERT INTO senha(id_senha, agencia, conta, dv, senha) VALUES (uuid(), 20, 26, 0, '123456');

INSERT INTO senha(id_senha, agencia, conta, dv, senha) VALUES (uuid(), 20, 27, 0, '123456');

INSERT INTO senha(id_senha, agencia, conta, dv, senha) VALUES (uuid(), 20, 28, 0, '123456');

INSERT INTO senha(id_senha, agencia, conta, dv, senha) VALUES (uuid(), 20, 29, 0, '123456');

INSERT INTO limite (id_limite, agencia, conta, dv, valor_limite, valor_utilizado, timestamp_limite) VALUES (uuid(),10, 10, 0, 1000000.00, 0.00, '2020-02-10 09:00:00.000');

INSERT INTO limite (id_limite, agencia, conta, dv, valor_limite, valor_utilizado, timestamp_limite) VALUES (uuid(),10, 11, 0, 1000000.00, 0.00, '2020-02-10 09:00:00.000');

INSERT INTO limite (id_limite, agencia, conta, dv, valor_limite, valor_utilizado, timestamp_limite) VALUES (uuid(),10, 12, 0, 1000000.00, 0.00, '2020-02-10 09:00:00.000');

INSERT INTO limite (id_limite, agencia, conta, dv, valor_limite, valor_utilizado, timestamp_limite) VALUES (uuid(),10, 13, 0, 1000000.00, 0.00, '2020-02-10 09:00:00.000');

INSERT INTO limite (id_limite, agencia, conta, dv, valor_limite, valor_utilizado, timestamp_limite) VALUES (uuid(),10, 14, 0, 0.00, 0.00, '2020-02-10 09:00:00.000');

INSERT INTO limite (id_limite, agencia, conta, dv, valor_limite, valor_utilizado, timestamp_limite) VALUES (uuid(),10, 15, 0, 0.00, 0.00, '2020-02-10 09:00:00.000');

INSERT INTO limite (id_limite, agencia, conta, dv, valor_limite, valor_utilizado, timestamp_limite) VALUES (uuid(),10, 16, 0, 0.00, 0.00, '2020-02-10 09:00:00.000');

INSERT INTO limite (id_limite, agencia, conta, dv, valor_limite, valor_utilizado, timestamp_limite) VALUES (uuid(),10, 17, 0, 1000000.00, 0.00, '2020-02-10 09:00:00.000');

INSERT INTO limite (id_limite, agencia, conta, dv, valor_limite, valor_utilizado, timestamp_limite) VALUES (uuid(),10, 18, 0, 1000000.00, 0.00, '2020-02-10 09:00:00.000');

INSERT INTO limite (id_limite, agencia, conta, dv, valor_limite, valor_utilizado, timestamp_limite) VALUES (uuid(),10, 19, 0, 1000000.00, 0.00, '2020-02-10 09:00:00.000');

INSERT INTO limite (id_limite, agencia, conta, dv, valor_limite, valor_utilizado, timestamp_limite) VALUES (uuid(),20, 20, 0, 1000000.00, 0.00, '2020-02-10 09:00:00.000');

INSERT INTO limite (id_limite, agencia, conta, dv, valor_limite, valor_utilizado, timestamp_limite) VALUES (uuid(),20, 21, 0, 1000000.00, 0.00, '2020-02-10 09:00:00.000');

INSERT INTO limite (id_limite, agencia, conta, dv, valor_limite, valor_utilizado, timestamp_limite) VALUES (uuid(),20, 22, 0, 1000000.00, 0.00, '2020-02-10 09:00:00.000');

INSERT INTO limite (id_limite, agencia, conta, dv, valor_limite, valor_utilizado, timestamp_limite) VALUES (uuid(),20, 23, 0, 1000000.00, 0.00, '2020-02-10 09:00:00.000');

INSERT INTO limite (id_limite, agencia, conta, dv, valor_limite, valor_utilizado, timestamp_limite) VALUES (uuid(),20, 24, 0, 110.00, 0.00, '2020-02-10 09:00:00.000');

INSERT INTO limite (id_limite, agencia, conta, dv, valor_limite, valor_utilizado, timestamp_limite) VALUES (uuid(),20, 25, 0, 50000.00, 0.00, '2020-02-10 09:00:00.000');

INSERT INTO limite (id_limite, agencia, conta, dv, valor_limite, valor_utilizado, timestamp_limite) VALUES (uuid(),20, 26, 0, 100000.00, 0.00, '2020-02-10 09:00:00.000');

INSERT INTO limite (id_limite, agencia, conta, dv, valor_limite, valor_utilizado, timestamp_limite) VALUES (uuid(),20, 27, 0, 100000.00, 0.00, '2020-02-10 09:00:00.000');

INSERT INTO limite (id_limite, agencia, conta, dv, valor_limite, valor_utilizado, timestamp_limite) VALUES (uuid(),20, 28, 0, 100000.00, 0.00, '2020-02-10 09:00:00.000');

INSERT INTO limite (id_limite, agencia, conta, dv, valor_limite, valor_utilizado, timestamp_limite) VALUES (uuid(),20, 29, 0, 100000.00, 0.00, '2020-02-10 09:00:00.000');

INSERT INTO conta (id_conta, agencia, conta, dv, valor_saldo, bloqueio) VALUES (uuid(), 10, 10, 0, 1000000.00, 0);

INSERT INTO conta (id_conta, agencia, conta, dv, valor_saldo, bloqueio) VALUES (uuid(), 10, 11, 0, 1000000.00, 0);

INSERT INTO conta (id_conta, agencia, conta, dv, valor_saldo, bloqueio) VALUES (uuid(), 10, 12, 0, 1000000.00, 0);

INSERT INTO conta (id_conta, agencia, conta, dv, valor_saldo, bloqueio) VALUES (uuid(), 10, 13, 0, 1000000.00, 0);

INSERT INTO conta (id_conta, agencia, conta, dv, valor_saldo, bloqueio) VALUES (uuid(), 10, 14, 0, 1000000.00, 0);

INSERT INTO conta (id_conta, agencia, conta, dv, valor_saldo, bloqueio) VALUES (uuid(), 10, 15, 0, 0.00, 0);

INSERT INTO conta (id_conta, agencia, conta, dv, valor_saldo, bloqueio) VALUES (uuid(), 10, 16, 0, 0.00, 0);

INSERT INTO conta (id_conta, agencia, conta, dv, valor_saldo, bloqueio) VALUES (uuid(), 10, 17, 0, 0.00, 0);

INSERT INTO conta (id_conta, agencia, conta, dv, valor_saldo, bloqueio) VALUES (uuid(), 10, 18, 0, 0.00, 0);

INSERT INTO conta (id_conta, agencia, conta, dv, valor_saldo, bloqueio) VALUES (uuid(), 10, 19, 0, 0.0, 0);

INSERT INTO conta (id_conta, agencia, conta, dv, valor_saldo, bloqueio) VALUES (uuid(), 20, 20, 0, 1000000.00, 1);

INSERT INTO conta (id_conta, agencia, conta, dv, valor_saldo, bloqueio) VALUES (uuid(), 20, 21, 0, 1000000.00, 1);

INSERT INTO conta (id_conta, agencia, conta, dv, valor_saldo, bloqueio) VALUES (uuid(), 20, 22, 0, 1000000.00, 1);

INSERT INTO conta (id_conta, agencia, conta, dv, valor_saldo, bloqueio) VALUES (uuid(), 20, 23, 0, 1000000.00, 1);

INSERT INTO conta (id_conta, agencia, conta, dv, valor_saldo, bloqueio) VALUES (uuid(), 20, 24, 0, 1000000.00, 1);

INSERT INTO conta (id_conta, agencia, conta, dv, valor_saldo, bloqueio) VALUES (uuid(), 20, 25, 0, 0.00, 1);

INSERT INTO conta (id_conta, agencia, conta, dv, valor_saldo, bloqueio) VALUES (uuid(), 20, 26, 0, 0.00, 1);

INSERT INTO conta (id_conta, agencia, conta, dv, valor_saldo, bloqueio) VALUES (uuid(), 20, 27, 0, 0.00, 1);

INSERT INTO conta (id_conta, agencia, conta, dv, valor_saldo, bloqueio) VALUES (uuid(), 20, 28, 0, 0.00, 1);

INSERT INTO conta (id_conta, agencia, conta, dv, valor_saldo, bloqueio) VALUES (uuid(), 20, 29, 0, 0.00, 1);

