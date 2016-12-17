package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Configuration
public class AppConfig {

    private static final String USER_AGENT = "Demo HTTP Client";
    public static final String DEFAULT_CONTENT_TYPE = MediaType.APPLICATION_JSON_VALUE;

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(
                Arrays.asList(new UserAgentInterceptor(), new ContentTypeInterceptor())
        );
        return restTemplate;
    }

    private class UserAgentInterceptor implements ClientHttpRequestInterceptor {

        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
            HttpHeaders requestHeaders = request.getHeaders();
            List<String> userAgent = requestHeaders.get(HttpHeaders.USER_AGENT);
            if (CollectionUtils.isEmpty(userAgent)) {
                requestHeaders.add(HttpHeaders.USER_AGENT, USER_AGENT);
            }
            return execution.execute(request, body);
        }

    }

    private class ContentTypeInterceptor implements ClientHttpRequestInterceptor {

        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
            HttpHeaders requestHeaders = request.getHeaders();
            if (isModifiableMethod(request.getMethod())
                    && !requestHeaders.containsKey(HttpHeaders.CONTENT_TYPE)) {
                requestHeaders.add(HttpHeaders.CONTENT_TYPE, DEFAULT_CONTENT_TYPE);
            }
            return execution.execute(request, body);
        }

        private boolean isModifiableMethod(HttpMethod method) {
            return method == HttpMethod.DELETE
                    || method == HttpMethod.PATCH
                    || method == HttpMethod.POST
                    || method == HttpMethod.PUT;
        }
    }

}
