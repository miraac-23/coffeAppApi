package com.example.coffeApp.controller;

import com.example.coffeApp.dto.availableProduct.AvailableProductCreateDto;
import com.example.coffeApp.dto.availableProduct.AvailableProductDto;
import com.example.coffeApp.service.availableProduct.AvailableProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/available-product")
public class AvailableProductController {

    private final AvailableProductService availableProductService;

    public AvailableProductController(AvailableProductService availableProductService) {
        this.availableProductService = availableProductService;
    }

    @PostMapping
    public ResponseEntity<AvailableProductDto> create(@RequestBody AvailableProductCreateDto availableProductCreateDto) {
        return new ResponseEntity<>(availableProductService.create(availableProductCreateDto), HttpStatus.CREATED);
    }

    @PostMapping("/update")
    public ResponseEntity<AvailableProductDto> update(@RequestBody AvailableProductDto availableProductDto) {
        return ResponseEntity.ok(availableProductService.update(availableProductDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AvailableProductDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(availableProductService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<AvailableProductDto>> getAll() {
        return ResponseEntity.ok(availableProductService.getAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        availableProductService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
