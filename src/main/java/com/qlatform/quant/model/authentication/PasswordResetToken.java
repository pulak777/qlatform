package com.qlatform.quant.model.authentication;

import com.qlatform.quant.model.User;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "password_reset_tokens")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetToken {
    @Id
    private String id;

    private String token;

    @DBRef
    private User user;

    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;

    private boolean used;
}