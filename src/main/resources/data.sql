INSERT INTO category (id, code, name, updated_at)
VALUES (1, 'CAT0001', 'Category 1', NOW())
    ON CONFLICT DO NOTHING;

INSERT INTO item (id, sku, name, price, stock, category_id, updated_at)
VALUES (1, 'SKU0001', 'Item 1', 99.99, 10, 1, NOW())
    ON CONFLICT DO NOTHING;
