# Add Contato

# --- !Ups
CREATE TABLE Contato (
    id bigint(20) NOT NULL AUTO_INCREMENT,
    nome varchar(255) NOT NULL,
    email varchar(255) NOT NULL,
    PRIMARY KEY (id)
);

# --- !Downs
DROP TABLE Contato;