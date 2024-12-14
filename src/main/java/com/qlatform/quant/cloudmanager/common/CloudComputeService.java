package com.qlatform.quant.cloudmanager.common;

import com.qlatform.quant.model.User;
import com.qlatform.quant.model.compute.ComputeInstance;
import com.qlatform.quant.model.dto.compute.ComputeInstanceRequest;

import java.util.List;

public interface CloudComputeService {
    List<ComputeInstance> listInstances(User user, String credentialNickname);
    ComputeInstance createInstance(User user, String credentialNickname, ComputeInstanceRequest request);
    void stopInstance(User user, String instanceId);
    void startInstance(User user, String instanceId);
    void terminateInstance(User user, String instanceId);
}