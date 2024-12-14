package com.qlatform.quant.model.credential;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EncryptedCredentialEntry {
    private String encryptedCredentials;
    private String iv;
    private String nickname;
    private String provider;  // AWS, GCP, Azure, etc.
    private String region;
    private LocalDateTime lastUpdatedAt;
}