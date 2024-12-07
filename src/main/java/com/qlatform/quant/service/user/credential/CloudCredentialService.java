package com.qlatform.quant.service.user.credential;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qlatform.quant.exception.credential.CredentialException;
import com.qlatform.quant.model.credential.CloudCredential;
import com.qlatform.quant.model.credential.CredentialSummary;
import com.qlatform.quant.model.credential.EncryptedCredentialEntry;
import com.qlatform.quant.model.credential.EncryptedData;
import com.qlatform.quant.repository.userdb.CloudCredentialRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.security.auth.login.CredentialNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class CloudCredentialService {
    private final CloudCredentialRepository repository;
    private final EncryptionService encryptionService;
    private final ObjectMapper objectMapper;

    @Autowired
    public CloudCredentialService(
            CloudCredentialRepository repository,
            EncryptionService encryptionService,
            ObjectMapper objectMapper) {
        this.repository = repository;
        this.encryptionService = encryptionService;
        this.objectMapper = objectMapper;
    }

    public void storeCredential(String clientId, String nickname,
                                Map<String, String> credentials,
                                String provider) {
        try {
            String credentialsJson = objectMapper.writeValueAsString(credentials);
            EncryptedData encryptedData = encryptionService.encrypt(credentialsJson);

            CloudCredential cloudCredential = repository.findByClientId(clientId)
                    .orElse(new CloudCredential());
            cloudCredential.setClientId(clientId);

            EncryptedCredentialEntry entry = new EncryptedCredentialEntry(
                    encryptedData.getEncryptedContent(),
                    encryptedData.getIv(),
                    nickname,
                    provider,
                    LocalDateTime.now()
            );

            cloudCredential.getCredentials().put(nickname, entry);
            repository.save(cloudCredential);

            log.info("Stored credentials '{}' for client: {}", nickname, clientId);
        } catch (Exception e) {
            log.error("Error storing credentials '{}' for client: {}", nickname, clientId, e);
            throw new CredentialException.CredentialStorageException("Failed to store credentials", e);
        }
    }

    public Map<String, String> retrieveCredential(String clientId, String nickname) {
        try {
            CloudCredential cloudCredential = repository.findByClientId(clientId)
                    .orElseThrow(() -> new CredentialNotFoundException("Credentials not found for client: " + clientId));

            EncryptedCredentialEntry entry = cloudCredential.getCredentials().get(nickname);
            if (entry == null) {
                throw new CredentialNotFoundException(
                        String.format("Credential '%s' not found for client: %s", nickname, clientId)
                );
            }

            String decryptedData = encryptionService.decrypt(
                    entry.getEncryptedCredentials(),
                    entry.getIv()
            );

            return objectMapper.readValue(
                    decryptedData,
                    new TypeReference<Map<String, String>>() {}
            );
        } catch (Exception e) {
            log.error("Error retrieving credentials '{}' for client: {}", nickname, clientId, e);
            throw new CredentialException.CredentialRetrievalException("Failed to retrieve credentials", e);
        }
    }

    public List<CredentialSummary> listClientCredentials(String clientId) throws CredentialNotFoundException {
        CloudCredential credential = repository.findByClientId(clientId)
                .orElseThrow(() -> new CredentialNotFoundException("Credentials not found for client: " + clientId));

        return credential.getCredentials().values().stream()
                .map(entry -> new CredentialSummary(
                        entry.getNickname(),
                        entry.getProvider(),
                        entry.getLastUpdatedAt()
                ))
                .collect(Collectors.toList());
    }

    public void deleteCredential(String clientId, String nickname) {
        try {
            CloudCredential credential = repository.findByClientId(clientId)
                    .orElseThrow(() -> new CredentialNotFoundException("Credentials not found for client: " + clientId));

            if (credential.getCredentials().remove(nickname) == null) {
                throw new CredentialNotFoundException(
                        String.format("Credential '%s' not found for client: %s", nickname, clientId)
                );
            }

            repository.save(credential);
            log.info("Deleted credential '{}' for client: {}", nickname, clientId);
        } catch (Exception e) {
            log.error("Error deleting credential '{}' for client: {}", nickname, clientId, e);
            throw new CredentialException.CredentialDeletionException("Failed to delete credential", e);
        }
    }
}
