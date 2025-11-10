package ma.projet.resource;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import jakarta.ws.rs.*; // Importe @GET, @POST, @Path, etc.
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import ma.projet.config.PersistenceManager; // Notre classe de l'étape précédente
import ma.projet.model.Category; // Notre entité
import ma.projet.model.Item;

import java.net.URI;
import java.util.List;

@Path("/categories") // URL de base pour cette classe: /api/categories
@Produces(MediaType.APPLICATION_JSON) // Par défaut, toutes les méthodes renvoient du JSON
@Consumes(MediaType.APPLICATION_JSON) // Par défaut, toutes les méthodes acceptent du JSON
public class CategoryResource {

    /**
     * Endpoint: GET /api/categories/{id}
     * Récupère une catégorie par son ID.
     */
    @GET
    @Path("/{id}")
    public Response getCategoryById(@PathParam("id") Long id) {
        EntityManager em = null;
        try {
            em = PersistenceManager.ENTITY_MANAGER_FACTORY.createEntityManager();

            Category category = em.find(Category.class, id);

            if (category == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Category not found")
                        .build();
            }
            return Response.ok(category).build();

        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * Endpoint: GET /api/categories/{id}/items?page=X&size=Y
     * Récupère les items pour une catégorie spécifique (pagination relationnelle).
     * C'est le endpoint  du document.
     */
    @GET
    @Path("/{id}/items") // Notez le chemin imbriqué
    public Response getItemsForCategory(
            @PathParam("id") Long categoryId,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {

        EntityManager em = null;
        try {
            em = PersistenceManager.ENTITY_MANAGER_FACTORY.createEntityManager();

            // On vérifie d'abord si la catégorie parente existe
            Category category = em.find(Category.class, categoryId);
            if (category == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Category not found")
                        .build();
            }

            // Requête pour trouver les Items de cette catégorie
            // Pas besoin de JOIN FETCH ici, car nous n'avons pas besoin de la catégorie (nous l'avons déjà)
            TypedQuery<Item> query = em.createQuery(
                    "SELECT i FROM Item i WHERE i.category.id = :cid ORDER BY i.name", Item.class);
            query.setParameter("cid", categoryId);

            // Appliquer la pagination
            query.setFirstResult(page * size);
            query.setMaxResults(size);

            List<Item> items = query.getResultList();

            return Response.ok(items).build();

        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * Endpoint: GET /api/categories?page=X&size=Y
     * Récupère une liste paginée de catégories.
     */
    @GET
    public Response getAllCategories(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {

        EntityManager em = null;
        try {
            em = PersistenceManager.ENTITY_MANAGER_FACTORY.createEntityManager();

            // Créer la requête JPQL pour sélectionner les catégories
            TypedQuery<Category> query = em.createQuery("SELECT c FROM Category c ORDER BY c.name", Category.class);

            // Appliquer la pagination
            query.setFirstResult(page * size); // L'offset (ex: page 2 * size 20 = offset 40)
            query.setMaxResults(size);         // La limite

            List<Category> categories = query.getResultList();

            return Response.ok(categories).build();

        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * Endpoint: POST /api/categories
     * Crée une nouvelle catégorie.
     */
    @POST
    public Response createCategory(Category category) {
        // Mettre l'ID à null pour s'assurer que c'est une création (JPA/Hibernate le gère)
        category.setId(null);

        EntityManager em = null;
        EntityTransaction transaction = null;
        try {
            em = PersistenceManager.ENTITY_MANAGER_FACTORY.createEntityManager();
            transaction = em.getTransaction();

            // Démarrer la transaction
            transaction.begin();

            // Sauvegarder l'entité (elle est maintenant "managed")
            em.persist(category);

            // Valider la transaction en base de données
            transaction.commit();

            // Renvoyer une réponse 201 Created avec l'URL de la nouvelle ressource
            URI createdUri = URI.create("/api/categories/" + category.getId());
            return Response.created(createdUri).entity(category).build();

        } catch (Exception e) {
            // En cas d'erreur, annuler la transaction
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            return Response.serverError().entity(e.getMessage()).build();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * Endpoint: PUT /api/categories/{id}
     * Met à jour une catégorie existante.
     */
    @PUT
    @Path("/{id}")
    public Response updateCategory(@PathParam("id") Long id, Category categoryData) {

        EntityManager em = null;
        EntityTransaction transaction = null;
        try {
            em = PersistenceManager.ENTITY_MANAGER_FACTORY.createEntityManager();
            transaction = em.getTransaction();

            // D'abord, trouver l'entité existante
            Category existingCategory = em.find(Category.class, id);

            if (existingCategory == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Category not found")
                        .build();
            }

            // Commencer la transaction
            transaction.begin();

            // Mettre à jour les champs de l'entité existante (elle est déjà "managed")
            existingCategory.setName(categoryData.getName());
            existingCategory.setCode(categoryData.getCode());
            // L'updated_at sera mis à jour automatiquement par @PreUpdate

            // Valider la transaction
            transaction.commit();

            return Response.ok(existingCategory).build();

        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            return Response.serverError().entity(e.getMessage()).build();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * Endpoint: DELETE /api/categories/{id}
     * Supprime une catégorie.
     */
    @DELETE
    @Path("/{id}")
    public Response deleteCategory(@PathParam("id") Long id) {

        EntityManager em = null;
        EntityTransaction transaction = null;
        try {
            em = PersistenceManager.ENTITY_MANAGER_FACTORY.createEntityManager();
            transaction = em.getTransaction();

            Category category = em.find(Category.class, id);

            if (category == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Category not found")
                        .build();
            }

            transaction.begin();

            // Supprimer l'entité
            em.remove(category);

            transaction.commit();

            // Renvoyer une réponse 204 No Content (succès, mais pas de contenu à renvoyer)
            return Response.noContent().build();

        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            // Gérer le cas où la catégorie est liée à des Items (contrainte de clé étrangère)
            return Response.status(Response.Status.CONFLICT)
                    .entity("Cannot delete category. It may be linked to existing items. Error: " + e.getMessage())
                    .build();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
}
