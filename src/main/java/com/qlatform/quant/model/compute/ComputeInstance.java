package com.qlatform.quant.model.compute;

import com.qlatform.quant.model.User;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Document(collection = "compute_instance")
public class ComputeInstance {
    @Id
    private String id;
    @DBRef
    private User user;
    @Indexed(unique = true)
    private String instanceId;
    private String name;
    private String status;
    private String type;
    private String region;
    private Map<String, String> tags;
    private String publicIp;
    private String privateIp;
    private String provider;
    private String credentialNickname;
    @CreatedDate
    private LocalDateTime createdAt;
    private Instant launchedAt;
}
