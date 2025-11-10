package com.example.benchc.web;

import com.example.benchc.model.Item;
import com.example.benchc.repository.ItemRepository;
import com.example.benchc.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/items") @RequiredArgsConstructor
public class ItemController {

    private final ItemRepository repo;
    private final ItemService service;

    @GetMapping
    public Page<Item> list(@RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "20") int size){
        return service.list(PageRequest.of(page, size));
    }

    @GetMapping("/{id}")
    public Item get(@PathVariable Long id){
        return repo.findById(id).orElseThrow();
    }

    @GetMapping(params = "categoryId")
    public Page<Item> byCategory(@RequestParam Long categoryId,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "20") int size){
        return service.byCategory(categoryId, PageRequest.of(page, size));
    }

    @PostMapping
    public Item create(@Valid @RequestBody Item i){
        i.setId(null);
        return repo.save(i);
    }

    @PutMapping("/{id}")
    public Item update(@PathVariable Long id, @Valid @RequestBody Item i){
        repo.findById(id).orElseThrow();
        i.setId(id);
        return repo.save(i);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        repo.deleteById(id);
    }
}

