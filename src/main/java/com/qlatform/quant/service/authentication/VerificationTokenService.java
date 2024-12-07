package com.qlatform.quant.service.authentication;

import com.qlatform.quant.model.User;
import com.qlatform.quant.model.VerificationToken;
import com.qlatform.quant.repository.userdb.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VerificationTokenService {
    private final VerificationTokenRepository verificationTokenRepository;

    public void save(VerificationToken verificationToken) {
        verificationTokenRepository.save(verificationToken);
    }

    public VerificationToken createVerificationToken(User user) {
        VerificationToken verificationToken = VerificationToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(LocalDateTime.now().plusDays(1))
                .build();

        return verificationTokenRepository.save(verificationToken);
    }

    public Optional<VerificationToken> findByToken(String token) {
        return verificationTokenRepository.findByToken(token);
    }

    public void deleteByUser(User user) {
        verificationTokenRepository.deleteByUser(user);
    }
}
