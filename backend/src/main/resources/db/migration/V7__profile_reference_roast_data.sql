ALTER TABLE perfiles_tueste
    ADD COLUMN id_lote_referencia INT NULL,
    ADD COLUMN peso_entrada_referencia_kg DECIMAL(10,2) NULL,
    ADD COLUMN peso_salida_referencia_kg DECIMAL(10,2) NULL,
    ADD CONSTRAINT fk_perfil_lote_referencia
        FOREIGN KEY (id_lote_referencia) REFERENCES lotes_cafe(id_lote)
        ON UPDATE CASCADE ON DELETE SET NULL;
