package com.qlatform.quant.repository.userdb;

import com.qlatform.quant.model.User;
import com.qlatform.quant.model.authentication.VerificationToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends MongoRepository<VerificationToken, String> {
    Optional<VerificationToken> findByToken(String token);
    void deleteByUser(User user);
}