package com.qlatform.quant.service.authentication;

import com.qlatform.quant.exception.authentication.*;
import com.qlatform.quant.model.*;
import com.qlatform.quant.model.adapter.CustomUserDetails;
import com.qlatform.quant.model.authentication.*;
import com.qlatform.quant.model.dto.auth.AuthResponse;
import com.qlatform.quant.model.dto.auth.LoginRequest;
import com.qlatform.quant.model.dto.auth.SignupRequest;
import com.qlatform.quant.repository.userdb.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final VerificationTokenService verificationTokenService;
    private final PasswordResetTokenService passwordResetTokenService;
    private final EmailService emailService;

    public AuthResponse signup(@Valid SignupRequest request) {
        log.debug("Processing signup request for email: {}", request.getEmail());

        validateSignupRequest(request);

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .provider(AuthProvider.LOCAL)
                .createdAt(LocalDateTime.now())
                .role(Role.USER)
                .emailVerified(false)
                .build();

        userRepository.save(user);
        log.info("User registered successfully with email: {}", request.getEmail());

        // Send verification email
        String token = generateVerificationToken(user);
        emailService.sendVerificationEmail(user.getEmail(), "Welcome!", token);

        return generateAuthResponse(user);
    }

    public AuthResponse login(@Valid LoginRequest request) {
        log.debug("Processing login for email: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!user.isEmailVerified()) {
            throw new EmailNotVerifiedException(user.getEmail(), "Please verify your email before logging in");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        updateLastLogin(user);
        log.info("Login successful for email: {}", request.getEmail());

        return generateAuthResponse(user);
    }

    public AuthResponse refreshToken(String refreshToken) {
        return refreshTokenService.findByToken(refreshToken)
                .map(token -> {
                    User user = token.getUser();
                    String accessToken = jwtService.generateToken(new HashMap<>(), new CustomUserDetails(user), jwtService.getAccessTokenExpiration());
                    return AuthResponse.builder()
                            .accessToken(accessToken)
                            .refreshToken(refreshToken)
                            .tokenType("Bearer")
                            .build();
                })
                .orElseThrow(() -> new TokenRefreshException(refreshToken, "Invalid refresh token"));
    }

    public void logout(String refreshToken) {
        refreshTokenService.deleteByToken(refreshToken);
        SecurityContextHolder.clearContext();
        log.info("User logged out successfully");
    }

    private void validateSignupRequest(@Valid SignupRequest request) {
        String email = request.getEmail();
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(email, "Email is already in use");
        }
    }

    private void updateLastLogin(User user) {
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);
    }

    private AuthResponse generateAuthResponse(User user) {
        String accessToken = jwtService.generateToken(new HashMap<>(), new CustomUserDetails(user), jwtService.getAccessTokenExpiration());
        RefreshToken refreshToken = refreshTokenService.findValidTokenByUser(user)
                .orElseGet(() -> refreshTokenService.createRefreshToken(user));

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .expiresIn(jwtService.getAccessTokenExpiration())
                .build();
    }

    private String generateVerificationToken(User user) {
        VerificationToken verificationToken = verificationTokenService.createVerificationToken(user);
        return verificationToken.getToken();
    }

    public void verifyEmail(String token) {
        VerificationToken verificationToken = verificationTokenService.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException(token, "Invalid verification token"));

        if (verificationToken.isExpired()) {
            throw new TokenExpiredException(token, "Verification token has expired");
        }

        if (verificationToken.isUsed()) {
            throw new TokenAlreadyUsedException(token, "Token has already been used");
        }

        User user = verificationToken.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);

        verificationToken.setUsed(true);
        verificationTokenService.save(verificationToken);

        log.info("Email verified successfully for user: {}", user.getEmail());
    }

    public void resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (user.isEmailVerified()) {
            throw new EmailAlreadyVerifiedException(email, "Email is already verified");
        }

        // Delete any existing unused tokens
        verificationTokenService.deleteByUser(user);

        // Generate new token and send email
        String newToken = generateVerificationToken(user);
        emailService.sendVerificationEmail(user.getEmail(), "Email Verification", newToken);

        log.info("Verification email resent to: {}", email);
    }

    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String resetToken = UUID.randomUUID().toString();
        // Save resetToken with an expiry time
        PasswordResetToken passwordResetToken = passwordResetTokenService.createPasswordResetToken(user, resetToken);

        emailService.sendPasswordResetEmail(email, passwordResetToken.getExpiresAt(), resetToken);
        log.info("Password reset email sent to: {}", email);
    }

    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenService.validatePasswordResetToken(token);

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetToken.setUsed(true);
        passwordResetTokenService.save(resetToken);

        log.info("Password reset successful for email: {}", user.getEmail());
    }
}
