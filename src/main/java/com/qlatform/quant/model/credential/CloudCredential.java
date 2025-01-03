package com.qlatform.quant.model.credential;

import com.qlatform.quant.model.User;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document("cloud_credential")
public class CloudCredential {
    @Id
    private String id;

    @Indexed(unique = true)
    @DBRef
    private User user;

    @Singular
    private Map<String, EncryptedCredentialEntry> credentials;

    @NonNull
    private String iv;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime lastUpdatedAt;
}
