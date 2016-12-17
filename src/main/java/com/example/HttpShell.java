package com.example;

import com.example.config.AppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.stream.Collectors;

@Component
public class HttpShell implements CommandMarker {

    private final RestTemplate restTemplate;

    @Autowired
    public HttpShell(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @CliCommand(value = "get", help = "Execute HTTP GET")
    public String get(
            @CliOption(key = {"url"}, mandatory = true, help = "URL of resource") final String url
    ) {
        return serializeResponse(restTemplate.getForEntity(url, String.class));
    }

    @CliCommand(value = "post", help = "Execute HTTP POST")
    public String post(
            @CliOption(key = {"url"}, mandatory = true, help = "URL of resource") final String url,
            @CliOption(key = {"body"}, help = "Payload") final String body,
            @CliOption(key = {"content-type"}, unspecifiedDefaultValue = AppConfig.DEFAULT_CONTENT_TYPE,
                    help = "Content type of the body") final String contentType) {
        HttpHeaders headers = defaultPostHeaders(contentType);

        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        return serializeResponse(restTemplate.exchange(url, HttpMethod.POST, entity, String.class));
    }

    private HttpHeaders defaultPostHeaders(String contentType) {
        HttpHeaders headers = new HttpHeaders();
        String defaultContentType = null == contentType ?
                AppConfig.DEFAULT_CONTENT_TYPE
                : contentType;
        headers.set(HttpHeaders.CONTENT_TYPE, defaultContentType);
        return headers;
    }


    private String serializeResponse(ResponseEntity<String> response) {
        StringBuilder sb = new StringBuilder();
        sb.append("--- HEADERS ---\n");
        sb.append(serializeHeaders(response.getHeaders()));

        sb.append("\n--- BODY ---\n");
        sb.append(response.getBody());
        return sb.toString();
    }

    private String serializeHeaders(HttpHeaders headers) {
        return headers.entrySet().stream()
                .map(entry -> String.format("%s: %s\n", entry.getKey(), entry.getValue()))
                .collect(Collectors.joining());
    }
}
