package com.qlatform.quant.service.authentication;

import com.qlatform.quant.model.authentication.RefreshToken;
import com.qlatform.quant.model.User;
import com.qlatform.quant.repository.userdb.RefreshTokenRepository;
import com.qlatform.quant.repository.userdb.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(LocalDateTime.now().plusDays(7))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public void deleteByToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }

    public Optional<RefreshToken> findValidTokenByUser(User user) {
        cleanupExpiredTokens();
        return refreshTokenRepository.findByUserAndExpiryDateAfter(
                user,
                LocalDateTime.now()
        );
    }

    public void cleanupExpiredTokens() {
        refreshTokenRepository.deleteByExpiryDateBefore(LocalDateTime.now());
    }

//    @Scheduled(cron = "0 0 0 * * *") // Run daily
//    public void scheduledCleanupExpiredTokens() {
//        cleanupExpiredTokens();
//    }
}
