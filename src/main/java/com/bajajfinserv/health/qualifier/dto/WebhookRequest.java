package com.bajajfinserv.health.qualifier.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebhookRequest {
    private String name;
    private String regNo;
    private String email;
}
