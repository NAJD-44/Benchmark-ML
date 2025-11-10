package com.example.benchc.web;

import com.example.benchc.model.Category;
import com.example.benchc.repository.CategoryRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/categories") @RequiredArgsConstructor
public class CategoryController {

    private final CategoryRepository repo;

    @GetMapping
    public Page<Category> list(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "20") int size){
        return repo.findAll(PageRequest.of(page, size));
    }

    @GetMapping("/{id}")
    public Category get(@PathVariable Long id){
        return repo.findById(id).orElseThrow();
    }

    @PostMapping
    public Category create(@Valid @RequestBody Category c){
        c.setId(null);
        return repo.save(c);
    }

    @PutMapping("/{id}")
    public Category update(@PathVariable Long id, @Valid @RequestBody Category c){
        repo.findById(id).orElseThrow();
        c.setId(id);
        return repo.save(c);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        repo.deleteById(id);
    }
}

