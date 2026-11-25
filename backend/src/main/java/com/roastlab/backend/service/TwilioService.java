package com.roastlab.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Envía SMS y WhatsApp con la API REST de Twilio (sin SDK, solo HttpClient de Java).
 * Credenciales en application-local/prod.properties: twilio.account-sid, twilio.auth-token,
 * twilio.sms-from y twilio.whatsapp-from (con prefijo "whatsapp:").
 */
@Service
public class TwilioService {
    private static final Pattern SID = Pattern.compile("\"sid\"\\s*:\\s*\"([^\"]+)\"");
    private static final Pattern MESSAGE = Pattern.compile("\"message\"\\s*:\\s*\"([^\"]+)\"");

    private final String accountSid;
    private final String authToken;
    private final String smsFrom;
    private final String whatsappFrom;
    private final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(8)).build();

    public TwilioService(@Value("${twilio.account-sid:}") String accountSid,
                         @Value("${twilio.auth-token:}") String authToken,
                         @Value("${twilio.sms-from:}") String smsFrom,
                         @Value("${twilio.whatsapp-from:}") String whatsappFrom) {
        this.accountSid = accountSid;
        this.authToken = authToken;
        this.smsFrom = smsFrom;
        this.whatsappFrom = whatsappFrom;
    }

    public boolean smsDisponible() { return !accountSid.isBlank() && !authToken.isBlank() && !smsFrom.isBlank(); }
    public boolean whatsappDisponible() { return !accountSid.isBlank() && !authToken.isBlank() && !whatsappFrom.isBlank(); }

    /** @return SID del mensaje creado en Twilio. */
    public String enviarSms(String telefono, String texto) {
        if (!smsDisponible()) throw new IllegalStateException("Twilio SMS no está configurado.");
        return enviar(normalizar(telefono), smsFrom, texto);
    }

    public String enviarWhatsapp(String telefono, String texto) {
        if (!whatsappDisponible()) throw new IllegalStateException("Twilio WhatsApp no está configurado.");
        return enviar("whatsapp:" + normalizar(telefono), whatsappFrom, texto);
    }

    /** Deja el número en formato internacional E.164 (por defecto México +52 si vienen 10 dígitos). */
    static String normalizar(String telefono) {
        String t = telefono == null ? "" : telefono.replaceAll("[\\s\\-()]", "");
        if (t.startsWith("+")) return t;
        if (t.matches("\\d{10}")) return "+52" + t;
        return "+" + t;
    }

    private String enviar(String to, String from, String body) {
        try {
            String form = "To=" + enc(to) + "&From=" + enc(from) + "&Body=" + enc(body);
            String basic = Base64.getEncoder().encodeToString((accountSid + ":" + authToken).getBytes(StandardCharsets.UTF_8));
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.twilio.com/2010-04-01/Accounts/" + accountSid + "/Messages.json"))
                .timeout(Duration.ofSeconds(15))
                .header("Authorization", "Basic " + basic)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(form))
                .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 201) {
                Matcher m = SID.matcher(response.body());
                return m.find() ? m.group(1) : "sin-sid";
            }
            Matcher m = MESSAGE.matcher(response.body());
            throw new IllegalStateException("Twilio respondió " + response.statusCode()
                + (m.find() ? ": " + m.group(1) : ""));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Envío interrumpido.", e);
        } catch (java.io.IOException e) {
            throw new IllegalStateException("No se pudo contactar a Twilio: " + e.getMessage(), e);
        }
    }

    private static String enc(String v) { return URLEncoder.encode(v, StandardCharsets.UTF_8); }
}
