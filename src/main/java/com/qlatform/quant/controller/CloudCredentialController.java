package com.qlatform.quant.controller;

import com.qlatform.quant.model.adapter.CustomUserDetails;
import com.qlatform.quant.model.credential.CredentialSummary;
import com.qlatform.quant.model.dto.CredentialRequest;
import com.qlatform.quant.service.user.UserService;
import com.qlatform.quant.service.user.credential.CloudCredentialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.CredentialNotFoundException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/api/cloud-credentials")
@RequiredArgsConstructor
@Slf4j
public class CloudCredentialController {
    private final CloudCredentialService cloudCredentialService;

    @PostMapping("/{nickname}")
    public ResponseEntity<Void> storeCredential(
            @PathVariable String nickname,
            @Valid @RequestBody CredentialRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.debug("Storing credential for user: {}, nickname: {}", userDetails.user().getId(), nickname);

        cloudCredentialService.storeCredential(
                userDetails.user().getId(),
                nickname,
                request.getCredentials(),
                request.getProvider()
        );

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{nickname}")
    public ResponseEntity<Map<String, String>> getCredential(
            @PathVariable String nickname,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.debug("Retrieving credential for user: {}, nickname: {}", userDetails.user().getId(), nickname);

        Map<String, String> credentials = cloudCredentialService.retrieveCredential(
                userDetails.user().getId(),
                nickname
        );

        return ResponseEntity.ok(credentials);
    }

    @GetMapping
    public ResponseEntity<List<CredentialSummary>> listCredentials(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.debug("Listing credentials for user: {}", userDetails.user().getId());
        try {
            List<CredentialSummary> credentials = cloudCredentialService.listClientCredentials(
                    userDetails.user().getId()
            );

            return ResponseEntity.ok(credentials);
        } catch (CredentialNotFoundException e) {
            throw new RuntimeException("Credentials not found", e);
        }
    }

    @DeleteMapping("/{nickname}")
    public ResponseEntity<Void> deleteCredential(
            @PathVariable String nickname,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.debug("Deleting credential for user: {}, nickname: {}", userDetails.user().getId(), nickname);

        cloudCredentialService.deleteCredential(
                userDetails.user().getId(),
                nickname
        );

        return ResponseEntity.ok().build();
    }
}
