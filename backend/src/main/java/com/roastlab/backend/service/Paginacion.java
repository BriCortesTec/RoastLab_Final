package com.roastlab.backend.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

final class Paginacion {
    private Paginacion() {}

    /** Página base 0, tamaño entre 1 y 50. */
    static Pageable de(int page, int size, Sort sort) {
        return PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 50), sort);
    }

    static String limpiar(String texto) {
        return texto == null ? "" : texto.trim();
    }
}
