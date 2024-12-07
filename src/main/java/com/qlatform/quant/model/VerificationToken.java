package com.qlatform.quant.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "verification_token")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerificationToken {
    @Id
    private String id;

    @Indexed(unique = true)
    private String token;

    @NonNull
    @DBRef
    private User user;

    private LocalDateTime expiryDate;

    private boolean used;

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiryDate);
    }
}