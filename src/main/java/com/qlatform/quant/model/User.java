package com.qlatform.quant.model;

import com.qlatform.quant.model.authentication.AuthProvider;
import com.qlatform.quant.model.authentication.Role;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

@Document(collection = "user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    private String id;

    @Indexed(unique = true)
    @NonNull
    private String email;

    private String password;    // null for OAuth2 users

    @NonNull
    private String name;

    @Builder.Default
    private boolean emailVerified = false;

    @NonNull
    private AuthProvider provider;

    private String providerId;  // OAuth2 provider ID

    @Builder.Default
    @NonNull
    private Role role = Role.USER;

    @Builder.Default
    private boolean enabled = true;
    @Builder.Default
    private boolean blocked = false;

    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    private LocalDateTime lastLoginAt;

    private Map<String, Object> attributes;  // OAuth2 attributes
}