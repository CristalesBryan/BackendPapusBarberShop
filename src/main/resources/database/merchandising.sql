-- Merchandising: camisas, gorras, llaveros
CREATE TABLE IF NOT EXISTS productos_merch (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(200) NOT NULL,
    categoria VARCHAR(50) NOT NULL,
    descripcion TEXT,
    precio_base DECIMAL(10, 2) NOT NULL DEFAULT 0,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    permite_personalizacion BOOLEAN NOT NULL DEFAULT FALSE,
    es_nuevo BOOLEAN NOT NULL DEFAULT FALSE,
    badge VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS imagenes_producto_merch (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    producto_id BIGINT NOT NULL,
    s3_key VARCHAR(500) NOT NULL,
    url VARCHAR(1000) NOT NULL,
    orden INT NOT NULL DEFAULT 0,
    CONSTRAINT fk_img_merch_producto FOREIGN KEY (producto_id) REFERENCES productos_merch(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS variantes_producto_merch (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    producto_id BIGINT NOT NULL,
    talla VARCHAR(10) NOT NULL,
    precio DECIMAL(10, 2),
    stock INT NOT NULL DEFAULT 0,
    CONSTRAINT fk_var_merch_producto FOREIGN KEY (producto_id) REFERENCES productos_merch(id) ON DELETE CASCADE,
    CONSTRAINT uq_var_merch_talla UNIQUE (producto_id, talla)
);

CREATE TABLE IF NOT EXISTS ventas_merch (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    producto_id BIGINT NOT NULL,
    variante_id BIGINT,
    producto_nombre VARCHAR(200) NOT NULL,
    categoria VARCHAR(50) NOT NULL,
    talla VARCHAR(10),
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10, 2) NOT NULL,
    total DECIMAL(10, 2) NOT NULL,
    metodo_pago VARCHAR(50) NOT NULL,
    personalizacion_nombre VARCHAR(100),
    personalizacion_numero VARCHAR(20),
    fecha DATE NOT NULL,
    hora TIME NOT NULL,
    CONSTRAINT fk_venta_merch_producto FOREIGN KEY (producto_id) REFERENCES productos_merch(id),
    CONSTRAINT fk_venta_merch_variante FOREIGN KEY (variante_id) REFERENCES variantes_producto_merch(id)
);

CREATE INDEX idx_productos_merch_activo ON productos_merch(activo);
CREATE INDEX idx_ventas_merch_fecha ON ventas_merch(fecha);
