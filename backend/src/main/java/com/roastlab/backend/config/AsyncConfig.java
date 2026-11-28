package com.roastlab.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/** Permite enviar WhatsApp y correos sin hacer esperar la respuesta HTTP al usuario. */
@Configuration
@EnableAsync
public class AsyncConfig {
}
