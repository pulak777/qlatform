package com.qlatform.quant.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "refresh_token")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {
    @Id
    private String id;

    @Indexed(unique = true)
    private String token;

    @NonNull
    @DBRef
    private User user;

    @NonNull
    private LocalDateTime expiryDate;

    @CreatedDate
    private LocalDateTime createdAt;

    private boolean revoked = false;
}
