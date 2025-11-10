package com.example.benchc.service;

import com.example.benchc.model.Item;
import com.example.benchc.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service @RequiredArgsConstructor
public class ItemService {

    private final ItemRepository repo;

    @Value("${app.optimized-join:false}")
    private boolean optimized;

    public Page<Item> list(Pageable pageable){
        return optimized ? repo.findAllWithCategory(pageable) : repo.findAll(pageable);
    }

    public Page<Item> byCategory(Long categoryId, Pageable pageable){
        if (!optimized) return repo.findByCategoryId(categoryId, pageable);
        List<Item> all = repo.findByCategoryIdJoinFetch(categoryId);
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), all.size());
        List<Item> slice = all.subList(Math.min(start, all.size()), Math.min(end, all.size()));
        return new PageImpl<>(slice, pageable, all.size());
    }
}
