package com.example.benchc.repository;

import com.example.benchc.model.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Page<Item> findByCategoryId(Long categoryId, Pageable pageable);

    // Anti N+1 (fetch join) pour le filtre categoryId (pagination en service)
    @Query("""
      select i from Item i
      join fetch i.category c
      where c.id = :cid
    """)
    List<Item> findByCategoryIdJoinFetch(@Param("cid") Long categoryId);

    // Alternative paginable pour /items?page=&size= (charge category via EntityGraph)
    @EntityGraph(attributePaths = "category")
    @Query("select i from Item i")
    Page<Item> findAllWithCategory(Pageable pageable);
}
