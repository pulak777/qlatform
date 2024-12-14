package com.qlatform.quant.cloudmanager.aws;

import com.qlatform.quant.cloudmanager.common.CloudComputeService;
import com.qlatform.quant.exception.computeinstance.InstanceNotFoundException;
import com.qlatform.quant.exception.credential.CloudServiceException;
import com.qlatform.quant.exception.credential.CredentialException;
import com.qlatform.quant.model.User;
import com.qlatform.quant.model.compute.ComputeInstance;
import com.qlatform.quant.model.dto.compute.ComputeInstanceRequest;
import com.qlatform.quant.service.user.ComputeInstanceService;
import com.qlatform.quant.service.user.credential.CloudCredentialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2AsyncClient;
import software.amazon.awssdk.services.ec2.model.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AwsComputeService implements CloudComputeService {
    private final CloudCredentialService credentialService;
    private final ComputeInstanceService computeInstanceService;
    private final Map<String, Ec2AsyncClient> clientCache;

    @Autowired
    public AwsComputeService(CloudCredentialService credentialService, ComputeInstanceService computeInstanceService) {
        this.computeInstanceService = computeInstanceService;
        this.credentialService = credentialService;
        this.clientCache = new ConcurrentHashMap<>();
    }

    private Ec2AsyncClient getEC2Client(User user, String credentialNickname) {
        String cacheKey = user.getId() + ":" + credentialNickname;
        return clientCache.computeIfAbsent(cacheKey, key -> {
            try {
                Map<String, String> credentials = credentialService.retrieveCredential(
                        user, credentialNickname
                );

                String region = credentialService.retrieveRegion(user, credentialNickname);

                if (!credentials.containsKey("accessKey") || !credentials.containsKey("secretKey")) {
                    throw new CredentialException.InvalidCredentialException("Invalid credential type for AWS");
                }

                AwsBasicCredentials basicCredentials = AwsBasicCredentials.create(
                        credentials.get("accessKey"),
                        credentials.get("secretKey")
                );

                return Ec2AsyncClient.builder()
                        .region(Region.of(region))
                        .credentialsProvider(StaticCredentialsProvider.create(basicCredentials))
                        .build();
            } catch (Exception e) {
                log.error("Failed to create EC2 client for user {} and account {}",
                        user, credentialNickname, e);
                throw new CloudServiceException("Failed to create EC2 client", e);
            }
        });
    }

    private String getUserCredentialKey(User user, String credentialNickname) {
        return user.getId() + "-" + credentialNickname;
    }

    public List<ComputeInstance> listInstances(User user, String credentialNickname) {
        try {
            Ec2AsyncClient ec2AsyncClient = getEC2Client(user, credentialNickname);
            DescribeInstancesResponse result = ec2AsyncClient.describeInstances().join();
            List<ComputeInstance> computeInstances = new ArrayList<>();

            for (Reservation reservation : result.reservations()) {
                for (Instance ec2Instance : reservation.instances()) {
                    ComputeInstance computeInstance = new ComputeInstance();
                    computeInstance.setId(ec2Instance.instanceId());
                    computeInstance.setName(getInstanceName(ec2Instance.tags()));
                    computeInstance.setStatus(ec2Instance.state().nameAsString());
                    computeInstance.setType(ec2Instance.instanceTypeAsString());
                    computeInstance.setPublicIp(ec2Instance.publicIpAddress());
                    computeInstance.setPrivateIp(ec2Instance.privateIpAddress());
                    computeInstance.setTags(convertTags(ec2Instance.tags()));
                    computeInstance.setLaunchedAt(ec2Instance.launchTime());
                    computeInstances.add(computeInstance);
                }
            }
            return computeInstances;
        } catch (Exception e) {
            log.error("Failed to list instances for user {} and account {}",
                    user, credentialNickname, e);
            throw new CloudServiceException("Failed to list instances", e);
        }
    }

    public ComputeInstance createInstance(User user, String credentialNickname, ComputeInstanceRequest request) {
        try {
            Ec2AsyncClient ec2Client = getEC2Client(user, credentialNickname);

            RunInstancesRequest runRequest = RunInstancesRequest.builder()
                    .imageId(request.getImageId())
                    .instanceType(request.getType())
                    .minCount(1)
                    .maxCount(1)
                    .tagSpecifications(createTagSpecifications(request.getTags()))
                    .build();

            CompletableFuture<RunInstancesResponse> futureResponse = ec2Client.runInstances(runRequest);
            RunInstancesResponse response = futureResponse.join();

            Instance ec2Instance = response.instances().getFirst();

            ComputeInstance computeInstance = ComputeInstance.builder()
                    .user(user)
                    .instanceId(ec2Instance.instanceId())
                    .type(ec2Instance.instanceTypeAsString())
                    .status(ec2Instance.state().nameAsString())
                    .region(ec2Instance.placement().availabilityZone().substring(0, ec2Instance.placement().availabilityZone().length() - 1))
                    .tags(request.getTags())
                    .publicIp(ec2Instance.publicIpAddress())
                    .privateIp(ec2Instance.privateIpAddress())
                    .provider("AWS")
                    .credentialNickname(credentialNickname)
                    .launchedAt(ec2Instance.launchTime())
                    .build();

            // Persist the ComputeInstance to the database
            return computeInstanceService.saveComputeInstance(computeInstance);
        } catch (Exception e) {
            log.error("Failed to create instance for user {} and credential {}", user.getId(), credentialNickname, e);
            throw new CloudServiceException("Failed to create instance", e);
        }
    }

    public void stopInstance(User user, String instanceId) {
        ComputeInstance computeInstance = computeInstanceService.getComputeInstanceByInstanceId(instanceId)
                .orElseThrow(() -> new InstanceNotFoundException("Instance not found: " + instanceId));
        try {
            Ec2AsyncClient ec2AsyncClient = getEC2Client(user, computeInstance.getCredentialNickname());
            StopInstancesRequest request = StopInstancesRequest.builder()
                    .instanceIds(instanceId)
                    .build();

            CompletableFuture<StopInstancesResponse> futureResponse = ec2AsyncClient.stopInstances(request);
            futureResponse.join();

            // Update the status of the instance
            computeInstance.setStatus("stopping");
            computeInstanceService.saveComputeInstance(computeInstance);
        } catch (Exception e) {
            log.error("Failed to stop instance {} for user {} and credential {}",
                    instanceId, user.getId(), computeInstance.getCredentialNickname(), e);
            throw new CloudServiceException("Failed to stop instance", e);
        }
    }

    public void startInstance(User user, String instanceId) {
        ComputeInstance computeInstance = computeInstanceService.getComputeInstanceByInstanceId(instanceId)
                .orElseThrow(() -> new InstanceNotFoundException("Instance not found: " + instanceId));

        try {
            Ec2AsyncClient ec2AsyncClient = getEC2Client(user, computeInstance.getCredentialNickname());

            StartInstancesRequest request = StartInstancesRequest.builder()
                    .instanceIds(instanceId)
                    .build();

            CompletableFuture<StartInstancesResponse> futureResponse = ec2AsyncClient.startInstances(request);
            futureResponse.join();

            // Update the status of the instance
            computeInstance.setStatus("pending");
            computeInstanceService.saveComputeInstance(computeInstance);
        } catch (Exception e) {
            log.error("Failed to start instance {} for user {} and credential {}",
                    instanceId, user.getId(), computeInstance.getCredentialNickname(), e);
            throw new CloudServiceException("Failed to start instance", e);
        }
    }

    public void terminateInstance(User user, String instanceId) {
        ComputeInstance computeInstance = computeInstanceService.getComputeInstanceByInstanceId(instanceId)
                .orElseThrow(() -> new InstanceNotFoundException("Instance not found: " + instanceId));

        try {
            Ec2AsyncClient ec2AsyncClient = getEC2Client(user, computeInstance.getCredentialNickname());

            TerminateInstancesRequest request = TerminateInstancesRequest.builder()
                    .instanceIds(instanceId)
                    .build();

            CompletableFuture<TerminateInstancesResponse> futureResponse = ec2AsyncClient.terminateInstances(request);
            futureResponse.join();

            // Update the status of the instance
            computeInstance.setStatus("shutting-down");
            computeInstanceService.saveComputeInstance(computeInstance);
        } catch (Exception e) {
            log.error("Failed to terminate instance {} for user {} and credential {}",
                    instanceId, user.getId(), computeInstance.getCredentialNickname(), e);
            throw new CloudServiceException("Failed to terminate instance", e);
        }
    }

    private String getInstanceName(List<Tag> tags) {
        return tags.stream()
                .filter(tag -> "Name".equals(tag.key()))
                .map(Tag::value)
                .findFirst()
                .orElse("");
    }

    private Map<String, String> convertTags(List<Tag> tags) {
        return tags.stream()
                .collect(Collectors.toMap(
                        Tag::key,
                        Tag::value
                ));
    }

    private List<TagSpecification> createTagSpecifications(Map<String, String> tags) {
        List<Tag> ec2Tags = tags.entrySet().stream()
                .map(entry -> Tag.builder().key(entry.getKey()).value(entry.getValue()).build())
                .collect(Collectors.toList());

        return Collections.singletonList(
                TagSpecification.builder().resourceType(ResourceType.INSTANCE)
                        .tags(ec2Tags).build()
        );
    }
}