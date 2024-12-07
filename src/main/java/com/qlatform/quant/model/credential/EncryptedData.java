package com.qlatform.quant.model.credential;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EncryptedData {
    private String encryptedContent;
    private String iv;
}
