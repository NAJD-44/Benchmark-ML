package ma.projet.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;

@Provider // L'annotation la plus importante ! Elle dit à Jersey d'utiliser cette classe.
public class JacksonConfig implements ContextResolver<ObjectMapper> {

    private final ObjectMapper objectMapper;

    public JacksonConfig() {
        objectMapper = new ObjectMapper();

        // C'est ici que nous ajoutons le module manquant
        objectMapper.registerModule(new JavaTimeModule());

        // Dit à Jackson de ne pas convertir les dates en "timestamps" (chiffres),
        // mais en texte ISO (ex: "2025-11-06T14:01:00Z")
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public ObjectMapper getContext(Class<?> type) {
        return objectMapper;
    }
}
