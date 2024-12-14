package com.qlatform.quant.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CredentialRequest {
    @NotBlank(message = "Provider is required")
    private String provider;

    @NotBlank(message = "Region is required")
    private String region;

    @NotEmpty(message = "Credentials map cannot be empty")
    private Map<String, String> credentials;
}