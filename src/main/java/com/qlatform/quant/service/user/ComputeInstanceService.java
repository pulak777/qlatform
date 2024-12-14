package com.qlatform.quant.service.user;

import com.qlatform.quant.model.compute.ComputeInstance;
import com.qlatform.quant.model.User;
import com.qlatform.quant.repository.userdb.ComputeInstanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ComputeInstanceService {

    private final ComputeInstanceRepository computeInstanceRepository;

    /**
     * Create a new ComputeInstance and save it to the repository.
     */
    public ComputeInstance createComputeInstance(User user, String instanceId, String name, String status,
                                                 String type, String region, Map<String, String> tags,
                                                 String publicIp, String privateIp, String provider,
                                                 String credentialNickname, Instant launchedAt) {
        ComputeInstance computeInstance = ComputeInstance.builder()
                .user(user)
                .instanceId(instanceId)
                .name(name)
                .status(status)
                .type(type)
                .region(region)
                .tags(tags)
                .publicIp(publicIp)
                .privateIp(privateIp)
                .provider(provider)
                .credentialNickname(credentialNickname)
                .launchedAt(launchedAt)
                .build();
        return computeInstanceRepository.save(computeInstance);
    }

    public ComputeInstance saveComputeInstance(ComputeInstance computeInstance) {
        return computeInstanceRepository.save(computeInstance);
    }

    /**
     * Fetch all ComputeInstances for a given user.
     */
    public List<ComputeInstance> getComputeInstancesByUser(User user) {
        return computeInstanceRepository.findByUser(user)
                .stream()
                .toList();
    }

    /**
     * Find a ComputeInstance by its unique instanceId.
     */
    public Optional<ComputeInstance> getComputeInstanceByInstanceId(String instanceId) {
        return computeInstanceRepository.findByInstanceId(instanceId);
    }

    /**
     * Update an existing ComputeInstance by instanceId.
     */
    @Transactional
    public ComputeInstance updateComputeInstance(String instanceId, String name, String status,
                                                 String type, Map<String, String> tags,
                                                 String publicIp, String privateIp) {
        Optional<ComputeInstance> optionalInstance = computeInstanceRepository.findByInstanceId(instanceId);
        if (optionalInstance.isEmpty()) {
            throw new IllegalArgumentException("ComputeInstance with instanceId " + instanceId + " not found.");
        }

        ComputeInstance computeInstance = optionalInstance.get();
        computeInstance.setName(name);
        computeInstance.setStatus(status);
        computeInstance.setType(type);
        computeInstance.setTags(tags);
        computeInstance.setPublicIp(publicIp);
        computeInstance.setPrivateIp(privateIp);
        return computeInstanceRepository.save(computeInstance);
    }

    /**
     * Delete a ComputeInstance by its unique instanceId.
     */
    public void deleteComputeInstance(String instanceId) {
        Optional<ComputeInstance> optionalInstance = computeInstanceRepository.findByInstanceId(instanceId);
        if (optionalInstance.isEmpty()) {
            throw new IllegalArgumentException("ComputeInstance with instanceId " + instanceId + " not found.");
        }
        computeInstanceRepository.delete(optionalInstance.get());
    }

    /**
     * Check if a ComputeInstance exists for a specific user.
     */
    public boolean existsForUser(User user) {
        return computeInstanceRepository.existsByUser(user);
    }

    /**
     * Fetch all ComputeInstances.
     */
    public List<ComputeInstance> getAllComputeInstances() {
        return computeInstanceRepository.findAll();
    }

    /**
     * Update the status of a ComputeInstance by its instanceId.
     */
    @Transactional
    public void updateComputeInstanceStatus(String instanceId, String newStatus) {
        Optional<ComputeInstance> optionalInstance = computeInstanceRepository.findByInstanceId(instanceId);
        if (optionalInstance.isEmpty()) {
            throw new IllegalArgumentException("ComputeInstance with instanceId " + instanceId + " not found.");
        }
        ComputeInstance computeInstance = optionalInstance.get();
        computeInstance.setStatus(newStatus);
        computeInstanceRepository.save(computeInstance);
    }

    /**
     * Add or update tags for a ComputeInstance.
     */
    @Transactional
    public void updateComputeInstanceTags(String instanceId, Map<String, String> newTags) {
        Optional<ComputeInstance> optionalInstance = computeInstanceRepository.findByInstanceId(instanceId);
        if (optionalInstance.isEmpty()) {
            throw new IllegalArgumentException("ComputeInstance with instanceId " + instanceId + " not found.");
        }
        ComputeInstance computeInstance = optionalInstance.get();
        computeInstance.getTags().putAll(newTags);
        computeInstanceRepository.save(computeInstance);
    }
}
