-- GameStore: script de creación de la base de datos (SQLite).
-- La aplicación lo ejecuta automáticamente al iniciar. Es seguro correrlo varias veces.

CREATE TABLE IF NOT EXISTS usuario (
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre     TEXT NOT NULL,
    usuario    TEXT NOT NULL UNIQUE,
    contrasena TEXT NOT NULL,
    rol        TEXT NOT NULL CHECK (rol IN ('ADMIN', 'CLIENTE')),
    direccion  TEXT
);

CREATE TABLE IF NOT EXISTS videojuego (
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre     TEXT NOT NULL,
    genero     TEXT NOT NULL,
    plataforma TEXT NOT NULL,
    precio     REAL NOT NULL CHECK (precio > 0),
    stock      INTEGER NOT NULL CHECK (stock >= 0)
);

CREATE TABLE IF NOT EXISTS compra (
    id        INTEGER PRIMARY KEY AUTOINCREMENT,
    id_cliente INTEGER NOT NULL REFERENCES usuario(id),
    fecha     TEXT NOT NULL,
    direccion TEXT NOT NULL,
    subtotal  REAL NOT NULL,
    descuento REAL NOT NULL,
    total     REAL NOT NULL,
    estado    TEXT NOT NULL CHECK (estado IN ('PENDIENTE', 'ENVIADO'))
);

CREATE TABLE IF NOT EXISTS detalle_compra (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    id_compra       INTEGER NOT NULL REFERENCES compra(id),
    id_videojuego   INTEGER NOT NULL REFERENCES videojuego(id),
    nombre          TEXT NOT NULL,
    cantidad        INTEGER NOT NULL CHECK (cantidad > 0),
    precio_unitario REAL NOT NULL
);

-- Datos iniciales: solo se cargan la primera vez (cuando todavía no hay usuarios)
INSERT INTO videojuego (nombre, genero, plataforma, precio, stock)
SELECT * FROM (
    SELECT 'The Legend of Zelda', 'Aventura', 'Switch', 59.99, 10
    UNION ALL SELECT 'FIFA 26', 'Deportes', 'PS5', 69.99, 15
    UNION ALL SELECT 'God of War', 'Acción', 'PS5', 49.99, 8
)
WHERE NOT EXISTS (SELECT 1 FROM usuario);

-- Administrador de prueba: usuario admin / contraseña admin123 (guardada como hash SHA-256)
INSERT OR IGNORE INTO usuario (id, nombre, usuario, contrasena, rol, direccion)
VALUES (1, 'Administrador Principal', 'admin',
        '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'ADMIN', NULL);
