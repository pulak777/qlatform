package com.qlatform.quant.repository.generaldb;

import com.qlatform.quant.model.QuantModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuantModelRepository extends MongoRepository<QuantModel, String> {
    List<QuantModel> findByName(String name);
}