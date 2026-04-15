-- Migracion idempotente: columnas de descuentos (PostgreSQL)
-- Coincide con entidades Servicio y VentaProducto (@Column snake_case, NUMERIC segun precision/scale)
-- Ejecutar en Railway si hiciera falta, o dejar que spring.sql.init lo aplique en prod (ver application-prod.properties)

-- ========== servicios ==========
ALTER TABLE servicios
    ADD COLUMN IF NOT EXISTS precio_original NUMERIC(10, 2);

ALTER TABLE servicios
    ADD COLUMN IF NOT EXISTS descuento_porcentaje NUMERIC(5, 2) DEFAULT 0;

UPDATE servicios
SET precio_original = COALESCE(precio_original, precio),
    descuento_porcentaje = COALESCE(descuento_porcentaje, 0)
WHERE precio_original IS NULL OR descuento_porcentaje IS NULL;

ALTER TABLE servicios
    ALTER COLUMN precio_original SET NOT NULL,
    ALTER COLUMN descuento_porcentaje SET NOT NULL;

-- ========== ventas_productos ==========
ALTER TABLE ventas_productos
    ADD COLUMN IF NOT EXISTS importe_original NUMERIC(10, 2);

ALTER TABLE ventas_productos
    ADD COLUMN IF NOT EXISTS descuento_porcentaje NUMERIC(5, 2) DEFAULT 0;

UPDATE ventas_productos
SET importe_original = COALESCE(importe_original, importe),
    descuento_porcentaje = COALESCE(descuento_porcentaje, 0)
WHERE importe_original IS NULL OR descuento_porcentaje IS NULL;

ALTER TABLE ventas_productos
    ALTER COLUMN importe_original SET NOT NULL,
    ALTER COLUMN descuento_porcentaje SET NOT NULL;
