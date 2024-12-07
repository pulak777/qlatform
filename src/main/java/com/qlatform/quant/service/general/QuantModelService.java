package com.qlatform.quant.service.general;

import com.qlatform.quant.exception.EntityNotFoundException;
import com.qlatform.quant.model.QuantModel;
import com.qlatform.quant.repository.generaldb.QuantModelRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class QuantModelService {
    private final QuantModelRepository quantModelRepository;

    @Autowired
    public QuantModelService(QuantModelRepository quantModelRepository) {
        this.quantModelRepository = quantModelRepository;
    }

    public QuantModel saveModel(QuantModel model) {
        return quantModelRepository.save(model);
    }

    public List<QuantModel> getAllModels() {
        return quantModelRepository.findAll();
    }

    public Optional<QuantModel> getModelById(String id) {
        return quantModelRepository.findById(id);
    }

    public List<QuantModel> getModelsByName(String name) {
        return quantModelRepository.findByName(name);
    }

    public QuantModel updateModel(String id, QuantModel updatedModel) {
        return quantModelRepository.findById(id)
                .map(existingModel -> {
                    existingModel.setName(updatedModel.getName());
                    existingModel.setDescription(updatedModel.getDescription());
                    existingModel.setDependencies(updatedModel.getDependencies());
                    existingModel.setCodeUrls(updatedModel.getCodeUrls());
                    existingModel.setPaperUrls(updatedModel.getPaperUrls());
                    return quantModelRepository.save(existingModel);
                })
                .orElseThrow(() -> new EntityNotFoundException("Model not found with id: " + id));
    }

    public void deleteModel(String id) {
        quantModelRepository.deleteById(id);
    }

    public List<QuantModel> saveAllModels(List<QuantModel> models) {
        return quantModelRepository.saveAll(models);
    }
}