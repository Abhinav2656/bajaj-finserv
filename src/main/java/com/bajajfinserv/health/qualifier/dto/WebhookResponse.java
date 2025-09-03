package com.bajajfinserv.health.qualifier.dto;

import lombok.Data;

@Data
public class WebhookResponse {
    private String webhook;
    private String accessToken;
}
