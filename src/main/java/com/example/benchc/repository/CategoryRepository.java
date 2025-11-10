package com.example.benchc.repository;

import com.example.benchc.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> { }
