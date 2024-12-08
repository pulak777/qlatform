package com.qlatform.quant.service.authentication;

import com.qlatform.quant.exception.authentication.*;
import com.qlatform.quant.model.authentication.PasswordResetToken;
import com.qlatform.quant.repository.userdb.PasswordResetTokenRepository;
import com.qlatform.quant.model.User;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PasswordResetTokenService {
    private final PasswordResetTokenRepository tokenRepository;

    public PasswordResetToken createPasswordResetToken(User user, String token) {
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusHours(1)) // Token expires after 1 hour
                .used(false)
                .build();
        return tokenRepository.save(resetToken);
    }

    public PasswordResetToken validatePasswordResetToken(String token) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException(token, "Invalid password reset token"));

        if (resetToken.isUsed()) {
            throw new TokenAlreadyUsedException(token, "Password reset token has already been used");
        }

        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException(token, "Password reset token has expired");
        }

        return resetToken;
    }

    public void deleteByUser(User user) {
        tokenRepository.deleteByUser(user);
    }

    public void save(PasswordResetToken token) {
        tokenRepository.save(token);
    }
}