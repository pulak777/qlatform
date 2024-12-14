package com.qlatform.quant.model.dto.compute;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ComputeInstanceRequest {
    private String name;
    private String type;
    private String imageId;
    private Map<String, String> tags;
}
