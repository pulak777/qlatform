package com.qlatform.quant.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document("quant_model")
public class QuantModel {
    @Id
    @Builder.Default
    @NonNull
    private String id = "QM_" + UUID.randomUUID().toString().replace("-", "");

    @NonNull
    private String name;

    @NonNull
    private String description;

    @Singular
    private List<PythonDependency> dependencies;

    @NonNull
    private List<String> codeUrls;

    @NonNull
    private List<String> paperUrls;
}
