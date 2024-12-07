package com.qlatform.quant.repository.userdb;

import com.qlatform.quant.model.credential.CloudCredential;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CloudCredentialRepository extends MongoRepository<CloudCredential, String> {
    Optional<CloudCredential> findByClientId(String clientId);
    boolean existsByClientId(String clientId);
}
