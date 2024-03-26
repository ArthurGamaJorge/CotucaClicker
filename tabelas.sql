CREATE SCHEMA CotucaClicker 

CREATE TABLE CotucaClicker.Usuarios(
	idUsuario INT PRIMARY KEY IDENTITY(1,1),
	nome VARCHAR(50) NOT NULL,
	pontos BIGINT NOT NULL
)