package ma.projet.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import ma.projet.config.PersistenceManager;
import ma.projet.model.Category;
import ma.projet.model.Item;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DataGenerator {

    // Configurez la taille des lots (batchs)
    private static final int BATCH_SIZE = 100;

    public static void main(String[] args) {
        System.out.println("--- Démarrage du générateur de données ---");

        EntityManager em = null;
        EntityTransaction transaction = null;
        List<Category> createdCategories = new ArrayList<>();
        Random rand = new Random();

        try {
            em = PersistenceManager.ENTITY_MANAGER_FACTORY.createEntityManager();
            transaction = em.getTransaction();

            // --- ÉTAPE 1 : Générer 2 000 Catégories ---
            System.out.println("Génération de 2 000 catégories...");
            transaction.begin();
            for (int i = 1; i <= 2000; i++) {
                Category cat = new Category();
                cat.setCode(String.format("CAT%04d", i)); // CAT0001, CAT0002...
                cat.setName("Category Name " + i);

                em.persist(cat);
                createdCategories.add(cat); // Stocke pour l'étape 2

                // Vider le lot (batch) et la mémoire
                if (i % BATCH_SIZE == 0) {
                    transaction.commit();
                    em.clear(); // Détache les objets de la session
                    System.out.println("... " + i + " catégories créées.");
                    transaction.begin();
                }
            }
            // Commit final pour les catégories restantes
            transaction.commit();
            System.out.println("--- 2 000 catégories générées avec succès ---");


            // --- ÉTAPE 2 : Générer 100 000 Items (50 par catégorie) ---
            System.out.println("Génération de 100 000 items...");
            transaction.begin();
            int totalItems = 0;

            // Pour chaque catégorie créée...
            for (Category category : createdCategories) {

                // ...créer 50 items
                for (int j = 1; j <= 50; j++) {
                    Item item = new Item();
                    totalItems++; // 1, 2, 3... 100000

                    item.setSku(String.format("SKU%06d", totalItems)); // SKU000001...
                    item.setName("Item Name " + totalItems);

                    // Prix aléatoire (ex: entre 10.00 et 500.00)
                    double randomPrice = 10.0 + (490.0 * rand.nextDouble());
                    item.setPrice(BigDecimal.valueOf(randomPrice).setScale(2, BigDecimal.ROUND_HALF_UP));

                    // Stock aléatoire (ex: entre 10 et 200)
                    item.setStock(10 + rand.nextInt(191));

                    // Lier l'item à sa catégorie
                    item.setCategory(category);

                    em.persist(item);

                    // Vider le lot (batch)
                    if (totalItems % BATCH_SIZE == 0) {
                        transaction.commit();
                        em.clear();
                        System.out.println("... " + totalItems + " items créés.");
                        transaction.begin();
                    }
                }
            }
            // Commit final pour les items restants
            transaction.commit();
            System.out.println("--- 100 000 items générés avec succès ---");

        } catch (Exception e) {
            System.err.println("ERREUR lors de la génération des données :");
            e.printStackTrace();
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
        } finally {
            if (em != null) {
                em.close();
            }
            PersistenceManager.closeEntityManagerFactory();
            System.out.println("--- Génération terminée ---");
        }
    }
}
