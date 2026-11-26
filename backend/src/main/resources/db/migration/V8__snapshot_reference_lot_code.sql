-- Conserva el codigo legible del lote en el perfil aunque el lote se elimine.
ALTER TABLE perfiles_tueste
    ADD COLUMN codigo_lote_referencia VARCHAR(40) NULL;

-- Completa la copia para perfiles que todavia tienen el lote relacionado.
UPDATE perfiles_tueste p
JOIN lotes_cafe l ON l.id_lote = p.id_lote_referencia
SET p.codigo_lote_referencia = l.codigo_lote
WHERE p.codigo_lote_referencia IS NULL;
