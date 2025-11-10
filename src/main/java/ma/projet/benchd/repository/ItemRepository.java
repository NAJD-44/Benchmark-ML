package ma.projet.benchd.repository;

import ma.projet.benchd.model.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

@RepositoryRestResource(collectionResourceRel = "items", path = "items")
public interface ItemRepository extends PagingAndSortingRepository<Item, Long> {

    // --- C'EST LE FILTRE PERSONNALISÉ ---
    // C'est la seule ligne de code dont nous avons besoin pour implémenter
    // le endpoint : GET /api/items/search/findByCategoryId?categoryId=...
    //[cite_start]// [cite: 66] (bien que le nom soit légèrement différent)

    @RestResource(path = "findByCategoryId", rel = "findByCategoryId")
    Page<Item> findByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);

    // Spring Data REST va automatiquement créer tous les autres endpoints :
    // GET /api/items
    // GET /api/items/{id}
    // POST /api/items
    // PUT /api/items/{id}
    // DELETE /api/items/{id}
}
