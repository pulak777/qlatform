package com.qlatform.quant.repository.userdb;

import com.qlatform.quant.model.User;
import com.qlatform.quant.model.compute.ComputeInstance;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComputeInstanceRepository extends MongoRepository<ComputeInstance, String> {
    Optional<List<ComputeInstance>> findByUser(User user);
    Optional<ComputeInstance> findByInstanceId(String instanceId);
    boolean existsByUser(User user);
}
