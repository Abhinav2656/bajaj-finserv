package com.bajajfinserv.health.qualifier.service;

import com.bajajfinserv.health.qualifier.dto.WebhookRequest;
import com.bajajfinserv.health.qualifier.dto.WebhookResponse;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class WebhookService {

    private final RestTemplate restTemplate;

    public WebhookService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void executeFlow() {
        try {
            // 1. Generate Webhook
            String url = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";
            WebhookRequest requestBody = new WebhookRequest("John Doe", "REG12347", "john@example.com");

            ResponseEntity<WebhookResponse> response =
                    restTemplate.postForEntity(url, requestBody, WebhookResponse.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                WebhookResponse body = response.getBody();
                if (body != null) {
                    String webhookUrl = body.getWebhook();
                    String accessToken = body.getAccessToken();

                // 2. Final SQL query
                String finalQuery =
                        "SELECT p.AMOUNT AS SALARY, " +
                        "CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) AS NAME, " +
                        "TIMESTAMPDIFF(YEAR, e.DOB, CURDATE()) AS AGE, " +
                        "d.DEPARTMENT_NAME " +
                        "FROM PAYMENTS p " +
                        "JOIN EMPLOYEE e ON p.EMP_ID = e.EMP_ID " +
                        "JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID " +
                        "WHERE DAY(p.PAYMENT_TIME) <> 1 " +
                        "ORDER BY p.AMOUNT DESC " +
                        "LIMIT 1;";

                Map<String, String> payload = new HashMap<>();
                payload.put("finalQuery", finalQuery);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setBearerAuth(accessToken);

                HttpEntity<Map<String, String>> entity = new HttpEntity<>(payload, headers);
                ResponseEntity<String> submitResponse = restTemplate.postForEntity(webhookUrl, entity, String.class);
                System.out.println("Submission Response: " + submitResponse.getBody());
                } else {
                    System.out.println("Webhook response body is null.");
                }
            } else {
                System.out.println("Failed to generate webhook. Response: " + response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
