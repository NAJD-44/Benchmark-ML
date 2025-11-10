package ma.projet.resource;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import ma.projet.config.PersistenceManager;
import ma.projet.model.Category;
import ma.projet.model.Item;

import java.net.URI;
import java.util.List;

@Path("/items")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ItemResource {

    /**
     * Endpoint: GET /api/items/{id}
     * Récupère un item par son ID.
     */
    @GET
    @Path("/{id}")
    public Response getItemById(@PathParam("id") Long id) {
        EntityManager em = null;
        try {
            em = PersistenceManager.ENTITY_MANAGER_FACTORY.createEntityManager();

            // Note: Nous utilisons "JOIN FETCH" ici pour charger la catégorie en même temps
            // C'est une optimisation pour éviter un N+1 si le client accède à item.category
            TypedQuery<Item> query = em.createQuery(
                    "SELECT i FROM Item i JOIN FETCH i.category WHERE i.id = :id", Item.class);
            query.setParameter("id", id);

            Item item = query.getSingleResult(); // getSingleResult lève NoResultException si non trouvé

            return Response.ok(item).build();

        } catch (jakarta.persistence.NoResultException e) {
            return Response.status(Response.Status.NOT_FOUND).entity("Item not found").build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * Endpoint: GET /api/items?page=X&size=Y
     * Endpoint: GET /api/items?categoryId=Z&page=X&size=Y
     * Récupère une liste paginée d'items, avec filtrage optionnel par categoryId.
     */
    @GET
    public Response getAllItems(
            @QueryParam("categoryId") Long categoryId,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {

        EntityManager em = null;
        try {
            em = PersistenceManager.ENTITY_MANAGER_FACTORY.createEntityManager();

            TypedQuery<Item> query;

            if (categoryId != null) {
                // --- Mode Filtrage (demandé par le benchmark) ---
                // C'est le mode "anti-N+1" avec JOIN FETCH
                query = em.createQuery(
                        "SELECT i FROM Item i JOIN FETCH i.category c WHERE c.id = :cid ORDER BY i.name", Item.class);
                query.setParameter("cid", categoryId);
            } else {
                // --- Mode Liste simple ---
                // C'est le mode "anti-N+1" avec JOIN FETCH
                query = em.createQuery(
                        "SELECT i FROM Item i JOIN FETCH i.category ORDER BY i.name", Item.class);
            }

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
     * Endpoint: POST /api/items
     * Crée un nouvel item.
     * Le JSON en entrée doit contenir: { ..., "category": { "id": 123 } }
     */
    @POST
    public Response createItem(Item item) {
        item.setId(null);

        EntityManager em = null;
        EntityTransaction transaction = null;
        try {
            em = PersistenceManager.ENTITY_MANAGER_FACTORY.createEntityManager();
            transaction = em.getTransaction();
            transaction.begin();

            // --- Gestion de la relation ---
            // Le JSON 'item' arrive avec un objet 'category' qui n'a que l'ID.
            // Nous devons récupérer la "vraie" entité Category depuis la BDD.
            if (item.getCategory() == null || item.getCategory().getId() == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Category ID is required")
                        .build();
            }

            Category category = em.find(Category.class, item.getCategory().getId());
            if (category == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Category not found")
                        .build();
            }
            // Attacher la vraie catégorie (managée) à notre nouvel item
            item.setCategory(category);

            em.persist(item);
            transaction.commit();

            URI createdUri = URI.create("/api/items/" + item.getId());
            return Response.created(createdUri).entity(item).build();

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
     * Endpoint: PUT /api/items/{id}
     * Met à jour un item existant.
     */
    @PUT
    @Path("/{id}")
    public Response updateItem(@PathParam("id") Long id, Item itemData) {

        EntityManager em = null;
        EntityTransaction transaction = null;
        try {
            em = PersistenceManager.ENTITY_MANAGER_FACTORY.createEntityManager();
            transaction = em.getTransaction();

            Item existingItem = em.find(Item.class, id);

            if (existingItem == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Item not found").build();
            }

            // --- Gestion de la relation ---
            Category category = null;
            if (itemData.getCategory() != null && itemData.getCategory().getId() != null) {
                category = em.find(Category.class, itemData.getCategory().getId());
                if (category == null) {
                    return Response.status(Response.Status.NOT_FOUND).entity("Category not found").build();
                }
            } else {
                return Response.status(Response.Status.BAD_REQUEST).entity("Category ID is required").build();
            }

            transaction.begin();

            // Mettre à jour les champs
            existingItem.setName(itemData.getName());
            existingItem.setSku(itemData.getSku());
            existingItem.setPrice(itemData.getPrice());
            existingItem.setStock(itemData.getStock());
            existingItem.setCategory(category); // Attacher la nouvelle catégorie

            transaction.commit();

            return Response.ok(existingItem).build();

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
     * Endpoint: DELETE /api/items/{id}
     * Supprime un item.
     */
    @DELETE
    @Path("/{id}")
    public Response deleteItem(@PathParam("id") Long id) {

        EntityManager em = null;
        EntityTransaction transaction = null;
        try {
            em = PersistenceManager.ENTITY_MANAGER_FACTORY.createEntityManager();
            transaction = em.getTransaction();

            Item item = em.find(Item.class, id);

            if (item == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Item not found").build();
            }

            transaction.begin();
            em.remove(item);
            transaction.commit();

            return Response.noContent().build();

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
}
