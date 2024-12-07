package com.qlatform.quant.controller;

import com.qlatform.quant.model.QuantModel;
import com.qlatform.quant.model.User;
import com.qlatform.quant.model.adapter.CustomUserDetails;
import com.qlatform.quant.service.general.QuantModelService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v1/api/model")
public class QuantModelController {
    private final QuantModelService quantModelService;

    @Autowired
    public QuantModelController(QuantModelService quantModelService) {
        this.quantModelService = quantModelService;
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.user();
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public List<QuantModel> getAllQuantModels() {
        return quantModelService.getAllModels();
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("/name/{name}")
    public List<QuantModel> getQuantModelByName(@PathVariable String name) {
        return quantModelService.getModelsByName(name);
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("/id/{id}")
    public Optional<QuantModel> getQuantModelById(@PathVariable String id) {
        return quantModelService.getModelById(id);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/save")
    public QuantModel saveQuantModel(@Valid @RequestBody QuantModel input) {
        return quantModelService.saveModel(input);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/save/batch")
    public List<QuantModel> batchSaveQuantModel(@Valid @RequestBody List<QuantModel> input) {
        return quantModelService.saveAllModels(input);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/update/{id}")
    public QuantModel updateQuantModel(@PathVariable String id, @Valid @RequestBody QuantModel input) {
        return quantModelService.updateModel(id, input);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/delete")
    public void deleteQuantModel(@RequestBody String id) {
        quantModelService.deleteModel(id);
    }
}