package com.roastlab.backend.service;

import com.roastlab.backend.dto.PhoneRules;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class WhatsAppService {

    private static final Pattern MESSAGE_ID =
        Pattern.compile("\"id\"\\s*:\\s*\"([^\"]+)\"");

    private static final Pattern ERROR_MESSAGE =
        Pattern.compile("\"message\"\\s*:\\s*\"([^\"]+)\"");

    private final String phoneNumberId;
    private final String accessToken;
    private final String apiVersion;

    private final HttpClient client = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .build();

    public WhatsAppService(
            @Value("${meta.whatsapp.phone-number-id:}") String phoneNumberId,
            @Value("${meta.whatsapp.access-token:}") String accessToken,
            @Value("${meta.whatsapp.api-version:v25.0}") String apiVersion) {
        this.phoneNumberId = phoneNumberId;
        this.accessToken = accessToken;
        this.apiVersion = apiVersion;
    }

    public boolean whatsappDisponible() {
        return !phoneNumberId.isBlank() && !accessToken.isBlank();
    }

    public String enviarWhatsapp(String telefono, String texto) {
        if (!whatsappDisponible()) {
            throw new IllegalStateException("WhatsApp Cloud API no está configurada.");
        }

        String destino = normalizar(telefono);

        String json = """
            {
              "messaging_product": "whatsapp",
              "recipient_type": "individual",
              "to": "%s",
              "type": "text",
              "text": {
                "preview_url": false,
                "body": "%s"
              }
            }
            """.formatted(destino, escaparJson(texto));

        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://graph.facebook.com/" + apiVersion
                    + "/" + phoneNumberId + "/messages"))
                .timeout(Duration.ofSeconds(20))
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

            HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                Matcher matcher = MESSAGE_ID.matcher(response.body());
                return matcher.find() ? matcher.group(1) : "enviado";
            }

            Matcher matcher = ERROR_MESSAGE.matcher(response.body());
            throw new IllegalStateException(
                "Meta respondió " + response.statusCode()
                    + (matcher.find() ? ": " + matcher.group(1) : "")
            );

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Envío de WhatsApp interrumpido.", e);
        } catch (java.io.IOException e) {
            throw new IllegalStateException(
                "No se pudo contactar a WhatsApp: " + e.getMessage(), e);
        }
    }

    public String enviarPlantillaTueste(
        String telefono,
        String nombreUsuario,
        String nombreTueste,
        String salidaKg,
        String maxTemp,
        String duracionMin) {

    if (!whatsappDisponible()) {
        throw new IllegalStateException(
            "WhatsApp Cloud API no está configurada."
        );
    }

    String destino = normalizar(telefono);

    String json = """
        {
          "messaging_product": "whatsapp",
          "to": "%s",
          "type": "template",
          "template": {
            "name": "roastlab_tueste_completado",
            "language": {
              "code": "es_MX"
            },
            "components": [
              {
                "type": "body",
                "parameters": [
                  {"type": "text", "text": "%s"},
                  {"type": "text", "text": "%s"},
                  {"type": "text", "text": "%s"},
                  {"type": "text", "text": "%s"},
                  {"type": "text", "text": "%s"}
                ]
              }
            ]
          }
        }
        """.formatted(
            destino,
            escaparJson(nombreUsuario),
            escaparJson(nombreTueste),
            escaparJson(salidaKg),
            escaparJson(maxTemp),
            escaparJson(duracionMin)
        );

    try {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(
                "https://graph.facebook.com/" + apiVersion
                + "/" + phoneNumberId + "/messages"
            ))
            .timeout(Duration.ofSeconds(20))
            .header("Authorization", "Bearer " + accessToken)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .build();

        HttpResponse<String> response =
            client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            Matcher matcher = MESSAGE_ID.matcher(response.body());
            return matcher.find() ? matcher.group(1) : "enviado";
        }

        Matcher matcher = ERROR_MESSAGE.matcher(response.body());

        throw new IllegalStateException(
            "Meta respondió " + response.statusCode()
                + (matcher.find() ? ": " + matcher.group(1) : "")
        );

    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new IllegalStateException(
            "Envío de WhatsApp interrumpido.", e
        );
    } catch (java.io.IOException e) {
        throw new IllegalStateException(
            "No se pudo contactar a WhatsApp: " + e.getMessage(), e
        );
    }
}

    /** La plantilla debe estar aprobada en Meta con el nombre roastlab_bienvenida. */
    public String enviarPlantillaBienvenida(String telefono, String nombre) {
        if (!whatsappDisponible()) {
            throw new IllegalStateException("WhatsApp Cloud API no está configurada.");
        }

        String json = """
            {
              "messaging_product": "whatsapp",
              "to": "%s",
              "type": "template",
              "template": {
                "name": "roastlab_bienvenida",
                "language": {"code": "es_MX"},
                "components": [{
                  "type": "body",
                  "parameters": [
                    {"type": "text", "text": "%s"}
                  ]
                }]
              }
            }
            """.formatted(PhoneRules.normalizar(telefono), escaparJson(nombre));

        return enviarJson(json);
    }

    private String enviarJson(String json) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://graph.facebook.com/" + apiVersion + "/" + phoneNumberId + "/messages"))
                .timeout(Duration.ofSeconds(20))
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                Matcher matcher = MESSAGE_ID.matcher(response.body());
                return matcher.find() ? matcher.group(1) : "enviado";
            }
            Matcher matcher = ERROR_MESSAGE.matcher(response.body());
            throw new IllegalStateException("Meta respondió " + response.statusCode()
                + (matcher.find() ? ": " + matcher.group(1) : ""));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Envío de WhatsApp interrumpido.", e);
        } catch (java.io.IOException e) {
            throw new IllegalStateException("No se pudo contactar a WhatsApp: " + e.getMessage(), e);
        }
    }

    private static String normalizar(String telefono) {
        return PhoneRules.normalizar(telefono);
    }

    private static String escaparJson(String texto) {
        return texto == null ? "" : texto
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t");
    }
}
