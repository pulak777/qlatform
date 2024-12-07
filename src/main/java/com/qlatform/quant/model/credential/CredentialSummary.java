package com.qlatform.quant.model.credential;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CredentialSummary {
    private String nickname;
    private String provider;
    private LocalDateTime lastUpdatedAt;
}