package com.example.coffeApp.controller;

import com.example.coffeApp.dto.coffe.CoffeCreateDto;
import com.example.coffeApp.dto.coffe.CoffeDto;
import com.example.coffeApp.dto.coffe.CoffeOrderResponseDto;
import com.example.coffeApp.service.coffe.CoffeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coffe")
@RequiredArgsConstructor
public class CoffeController {

    private final CoffeService coffeService;

    @PostMapping
    public ResponseEntity<CoffeDto> createCoffee(@RequestBody CoffeCreateDto coffeCreateDto) {
        CoffeDto created = coffeService.createCoffee(coffeCreateDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PostMapping("/update")
    public ResponseEntity<CoffeDto> update(@RequestBody CoffeDto coffeDto) {
        return ResponseEntity.ok(coffeService.update(coffeDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CoffeDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(coffeService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<CoffeDto>> getAll() {
        return ResponseEntity.ok(coffeService.getAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        coffeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/order/{id}")
    public ResponseEntity<CoffeOrderResponseDto> coffeOrder(@PathVariable Long id) {
        return ResponseEntity.ok(coffeService.coffeOrder(id));
    }

}
