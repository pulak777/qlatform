package com.qlatform.quant.model.dto;

import lombok.*;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PythonDependency {
    private String name;
    private String version;
}
