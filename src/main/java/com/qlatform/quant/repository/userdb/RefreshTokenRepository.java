package com.qlatform.quant.repository.userdb;

import com.qlatform.quant.model.RefreshToken;
import com.qlatform.quant.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends MongoRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByToken(String token);
    Optional<RefreshToken> findByUserAndExpiryDateAfter(User user, LocalDateTime now);
    void deleteByExpiryDateBefore(LocalDateTime dateTime);
}