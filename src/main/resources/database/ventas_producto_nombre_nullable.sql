-- Migración: permitir eliminar productos manteniendo el historial de ventas
-- Ejecutar solo si la base de datos no se actualiza con Hibernate ddl-auto=update
-- 1. Añadir columna para guardar el nombre del producto cuando se elimine del catálogo
ALTER TABLE ventas_productos ADD COLUMN IF NOT EXISTS producto_nombre VARCHAR(200);

-- 2. Rellenar producto_nombre con el nombre actual del producto (para registros existentes)
UPDATE ventas_productos vp
SET producto_nombre = p.nombre
FROM productos p
WHERE vp.producto_id = p.id AND (vp.producto_nombre IS NULL OR vp.producto_nombre = '');

-- 3. Permitir que producto_id sea NULL (ventas de productos ya eliminados)
ALTER TABLE ventas_productos ALTER COLUMN producto_id DROP NOT NULL;
