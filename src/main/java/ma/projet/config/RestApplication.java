package ma.projet.config;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

// C'est l'URL de base pour TOUS vos endpoints.
// Exemple : /api/categories, /api/items
@ApplicationPath("/api")
public class RestApplication extends Application {
    // Pour l'instant, cette classe peut rester vide.
    // Jersey va automatiquement scanner vos autres classes
    // pour trouver les endpoints (@Path).
}
