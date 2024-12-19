package com.qlatform.quant.service.user;

import com.qlatform.quant.model.User;
import com.qlatform.quant.model.authentication.AuthProvider;
import com.qlatform.quant.model.authentication.Role;
import com.qlatform.quant.model.compute.ComputeInstance;
import com.qlatform.quant.repository.userdb.ComputeInstanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ComputeInstanceServiceTest {

    @Mock
    private ComputeInstanceRepository computeInstanceRepository;

    @InjectMocks
    private ComputeInstanceService computeInstanceService;

    private User testUser;
    private ComputeInstance testInstance;
    private static final String INSTANCE_ID = "i-1234567890abcdef0";
    private Map<String, String> testTags;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id("user123")
                .email("test@example.com")
                .name("Test User")
                .provider(AuthProvider.LOCAL)
                .role(Role.USER)
                .emailVerified(true)
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .build();

        testTags = new HashMap<>();
        testTags.put("Name", "TestInstance");
        testTags.put("Environment", "Test");

        testInstance = ComputeInstance.builder()
                .user(testUser)
                .instanceId(INSTANCE_ID)
                .name("TestInstance")
                .status("running")
                .type("t2.micro")
                .region("us-west-2")
                .tags(testTags)
                .publicIp("1.2.3.4")
                .privateIp("10.0.0.1")
                .provider("AWS")
                .credentialNickname("test-credential")
                .launchedAt(Instant.now())
                .build();
    }

    @Test
    void createComputeInstance_Success() {
        when(computeInstanceRepository.save(any(ComputeInstance.class)))
                .thenReturn(testInstance);

        ComputeInstance result = computeInstanceService.createComputeInstance(
                testUser, INSTANCE_ID, "TestInstance", "running",
                "t2.micro", "us-west-2", testTags, "1.2.3.4",
                "10.0.0.1", "AWS", "test-credential", Instant.now());

        assertNotNull(result);
        assertEquals(INSTANCE_ID, result.getInstanceId());
        assertEquals(testUser, result.getUser());
        verify(computeInstanceRepository).save(any(ComputeInstance.class));
    }

    @Test
    void getComputeInstancesByUser_Success() {
        when(computeInstanceRepository.findByUser(testUser)).thenReturn(Optional.of(List.of(testInstance)));

        List<ComputeInstance> results = computeInstanceService.getComputeInstancesByUser(testUser);

        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertEquals(INSTANCE_ID, results.getFirst().getInstanceId());
        verify(computeInstanceRepository).findByUser(testUser);
    }

    @Test
    void getComputeInstanceByInstanceId_Success() {
        when(computeInstanceRepository.findByInstanceId(INSTANCE_ID))
                .thenReturn(Optional.of(testInstance));

        Optional<ComputeInstance> result = computeInstanceService.getComputeInstanceByInstanceId(INSTANCE_ID);

        assertTrue(result.isPresent());
        assertEquals(INSTANCE_ID, result.get().getInstanceId());
        verify(computeInstanceRepository).findByInstanceId(INSTANCE_ID);
    }

    @Test
    void updateComputeInstance_Success() {
        when(computeInstanceRepository.findByInstanceId(INSTANCE_ID))
                .thenReturn(Optional.of(testInstance));
        when(computeInstanceRepository.save(any(ComputeInstance.class)))
                .thenReturn(testInstance);

        ComputeInstance result = computeInstanceService.updateComputeInstance(
                INSTANCE_ID, "UpdatedName", "stopped",
                "t2.small", testTags, "5.6.7.8", "10.0.0.2");

        assertNotNull(result);
        assertEquals("UpdatedName", result.getName());
        assertEquals("stopped", result.getStatus());
        verify(computeInstanceRepository).save(any(ComputeInstance.class));
    }

    @Test
    void updateComputeInstance_NotFound() {
        when(computeInstanceRepository.findByInstanceId(INSTANCE_ID))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                computeInstanceService.updateComputeInstance(
                        INSTANCE_ID, "UpdatedName", "stopped",
                        "t2.small", testTags, "5.6.7.8", "10.0.0.2"));
    }

    @Test
    void deleteComputeInstance_Success() {
        when(computeInstanceRepository.findByInstanceId(INSTANCE_ID))
                .thenReturn(Optional.of(testInstance));

        computeInstanceService.deleteComputeInstance(INSTANCE_ID);

        verify(computeInstanceRepository).delete(testInstance);
    }

    @Test
    void deleteComputeInstance_NotFound() {
        when(computeInstanceRepository.findByInstanceId(INSTANCE_ID))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                computeInstanceService.deleteComputeInstance(INSTANCE_ID));
    }

    @Test
    void existsForUser_Success() {
        when(computeInstanceRepository.existsByUser(testUser))
                .thenReturn(true);

        assertTrue(computeInstanceService.existsForUser(testUser));
        verify(computeInstanceRepository).existsByUser(testUser);
    }

    @Test
    void getAllComputeInstances_Success() {
        when(computeInstanceRepository.findAll())
                .thenReturn(List.of(testInstance));

        List<ComputeInstance> results = computeInstanceService.getAllComputeInstances();

        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        verify(computeInstanceRepository).findAll();
    }

    @Test
    void updateComputeInstanceStatus_Success() {
        when(computeInstanceRepository.findByInstanceId(INSTANCE_ID))
                .thenReturn(Optional.of(testInstance));

        computeInstanceService.updateComputeInstanceStatus(INSTANCE_ID, "stopped");

        verify(computeInstanceRepository).save(argThat(instance ->
                "stopped".equals(instance.getStatus())
        ));
    }

    @Test
    void updateComputeInstanceTags_Success() {
        when(computeInstanceRepository.findByInstanceId(INSTANCE_ID))
                .thenReturn(Optional.of(testInstance));

        Map<String, String> newTags = new HashMap<>();
        newTags.put("Environment", "Production");

        computeInstanceService.updateComputeInstanceTags(INSTANCE_ID, newTags);

        verify(computeInstanceRepository).save(argThat(instance ->
                instance.getTags().containsKey("Environment") &&
                        "Production".equals(instance.getTags().get("Environment"))
        ));
    }

    @Test
    void saveComputeInstance_Success() {
        when(computeInstanceRepository.save(testInstance))
                .thenReturn(testInstance);

        ComputeInstance result = computeInstanceService.saveComputeInstance(testInstance);

        assertNotNull(result);
        assertEquals(INSTANCE_ID, result.getInstanceId());
        verify(computeInstanceRepository).save(testInstance);
    }
}