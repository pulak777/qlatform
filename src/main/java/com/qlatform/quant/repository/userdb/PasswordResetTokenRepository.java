package com.qlatform.quant.repository.userdb;

import com.qlatform.quant.model.User;
import com.qlatform.quant.model.authentication.PasswordResetToken;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends MongoRepository<PasswordResetToken, String> {
    Optional<PasswordResetToken> findByToken(String token);

    void deleteByUser(User user);
}

