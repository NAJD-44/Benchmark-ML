package com.example.benchc;

import com.example.benchc.model.Category;
import com.example.benchc.model.Item;
import com.example.benchc.repository.CategoryRepository;
import com.example.benchc.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Random;

/**
 * Génère un jeu de données réaliste pour le benchmark :
 * - 500 catégories (CAT0001 ... CAT0500)
 * - 50 items par catégorie (soit 25 000 items)
 */
@Component
@RequiredArgsConstructor
@Profile("dev")  // s'exécute seulement si tu lances avec --spring.profiles.active=dev
public class BootstrapData implements CommandLineRunner {

    private final CategoryRepository categoryRepo;
    private final ItemRepository itemRepo;

    @Override
    public void run(String... args) {
        long start = System.currentTimeMillis();

        if (categoryRepo.count() > 0) {
            System.out.println("✅ Données déjà présentes, aucune insertion.");
            return;
        }

        int catCount = 500;      // tu peux augmenter à 2000 selon ton PC
        int itemsPerCat = 50;    // 50 items par catégorie
        Random rnd = new Random(42);

        System.out.println("🚀 Génération de " + catCount + " catégories et " + (catCount * itemsPerCat) + " items...");

        for (int c = 1; c <= catCount; c++) {
            Category cat = Category.builder()
                    .code("CAT%04d".formatted(c))
                    .name("Category " + c)
                    .build();
            cat = categoryRepo.save(cat);

            for (int i = 1; i <= itemsPerCat; i++) {
                Item item = Item.builder()
                        .sku("SKU%04d-%03d".formatted(c, i))
                        .name("Item %d-%d".formatted(c, i))
                        .price(new BigDecimal(5 + rnd.nextInt(500)))
                        .stock(5 + rnd.nextInt(200))
                        .category(cat)
                        .build();
                itemRepo.save(item);
            }

            if (c % 50 == 0) {
                System.out.println(" - Progression : " + c + "/" + catCount + " catégories");
            }
        }

        long duration = System.currentTimeMillis() - start;
        System.out.printf("✅ Génération terminée en %.2f s%n", duration / 1000.0);
    }
}
