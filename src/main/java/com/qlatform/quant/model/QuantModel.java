package com.qlatform.quant.model;

import com.qlatform.quant.model.dto.PythonDependency;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document("quant_model")
public class QuantModel {
    @Id
    private String id;

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
