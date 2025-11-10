package ma.projet.config;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class PersistenceManager {

    // C'est ici que nous lisons le "persistence.xml"
    // Le nom "benchmark-pu" DOIT correspondre à celui dans votre persistence.xml
    public static final EntityManagerFactory ENTITY_MANAGER_FACTORY =
            Persistence.createEntityManagerFactory("benchmark-pu");

    // Méthode pour fermer la factory lorsque l'application s'arrête
    public static void closeEntityManagerFactory() {
        if (ENTITY_MANAGER_FACTORY != null && ENTITY_MANAGER_FACTORY.isOpen()) {
            ENTITY_MANAGER_FACTORY.close();
        }
    }
}
