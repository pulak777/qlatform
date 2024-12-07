package com.qlatform.quant.model.credential;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document("cloud_credential")
public class CloudCredential {
    @Id
    @Builder.Default
    @NonNull
    private String id = "CCM_" + UUID.randomUUID().toString().replace("-", "");

    @Indexed(unique = true)
    private String clientId;

    @Singular
    private Map<String, EncryptedCredentialEntry> credentials;

    @NonNull
    private String iv;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime lastUpdatedAt;
}
