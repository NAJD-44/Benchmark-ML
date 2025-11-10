package ma.projet.benchd.repository;

import ma.projet.benchd.model.Category;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

// C'est l'annotation magique.
// Elle dit à Spring de créer automatiquement les endpoints REST pour 'Category'.
@RepositoryRestResource(collectionResourceRel = "categories", path = "categories")
public interface CategoryRepository extends PagingAndSortingRepository<Category, Long> {

    // C'est tout !
    // Spring Data REST va automatiquement créer :
    // GET /api/categories
    // GET /api/categories/{id}
    // POST /api/categories
    // PUT /api/categories/{id}
    // DELETE /api/categories/{id}
    // ... et même les endpoints relationnels comme /api/categories/{id}/items
}
