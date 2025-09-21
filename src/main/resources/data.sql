DELETE FROM order_item;
DELETE FROM orders;
DELETE FROM product;
DELETE FROM category;
DELETE FROM users;

-- Insertar usuarios
INSERT INTO users (id, first_name, last_name, email, password, phone, address, role, active, created_at) VALUES
(1, 'Admin', 'Sistema', 'admin@ecommerce.com', 'password123', NULL, NULL, 'ADMIN', true, CURRENT_TIMESTAMP),
(2, 'Juan', 'Pérez', 'juan@email.com', 'password123', '987654321', 'Av. Siempre Viva 123', 'CUSTOMER', true, CURRENT_TIMESTAMP),
(3, 'María', 'Gómez', 'maria@email.com', 'password123', '912345678', 'Calle Falsa 456', 'CUSTOMER', true, CURRENT_TIMESTAMP);

-- Restablecer las secuencias (importante para PostgreSQL)
ALTER SEQUENCE users_id_seq RESTART WITH 4;
ALTER SEQUENCE category_id_seq RESTART WITH 7;
ALTER SEQUENCE product_id_seq RESTART WITH 13;

INSERT INTO category (id, name, description, created_at) VALUES
                                                             (1, 'Bicicletas de Montaña', 'Ideales para senderos y terrenos difíciles.', CURRENT_TIMESTAMP),
                                                             (2, 'Bicicletas de Ruta', 'Diseñadas para velocidad y largas distancias en carretera.', CURRENT_TIMESTAMP),
                                                             (3, 'Bicicletas Urbanas', 'Pensadas para comodidad y movilidad en la ciudad.', CURRENT_TIMESTAMP),
                                                             (4, 'Bicicletas Eléctricas', 'Con motor de asistencia al pedaleo y batería recargable.', CURRENT_TIMESTAMP),
                                                             (5, 'Bicicletas BMX', 'Para acrobacias, saltos y estilo libre.', CURRENT_TIMESTAMP),
                                                             (6, 'Bicicletas Gravel', 'Versátiles para carretera y caminos de tierra.', CURRENT_TIMESTAMP);


-- Montaña
INSERT INTO product (id, name, description, price, stock, created_at, image_url, category_id)
VALUES (1, 'Trek Marlin 7', 'Cuadro de aluminio, suspensión delantera y 21 velocidades.', 2800.00,
        8, NOW(), 'https://static.wixstatic.com/media/04ee8b_cff23937325545a1b9a5ff5bdb77ac26~mv2.jpg/v1/fill/w_602,h_602,al_c,q_85,usm_0.66_1.00_0.01,enc_avif,quality_auto/04ee8b_cff23937325545a1b9a5ff5bdb77ac26~mv2.jpg', 1),
       (2, 'Scott Aspect 950', 'Bicicleta con frenos de disco hidráulicos y transmisión Shimano.',
        3500.00, 4, NOW(), 'https://www.monark.com.pe/static/monark-pe/uploads/products/images/e-bike-monark-e-motion-20-negro-verde.jpg', 1);

-- Ruta
INSERT INTO product (id, name, description, price, stock, created_at, image_url, category_id)
VALUES (3, 'Specialized Allez', 'Ligera con componentes Shimano Claris, ideal para entrenamientos.',
        4500.00, 5, NOW(), 'https://static.wixstatic.com/media/04ee8b_1fcb4ce4bb7848078ebdb83cbbd71668~mv2.jpg/v1/fill/w_413,h_413,al_c,q_80,usm_0.66_1.00_0.01,enc_avif,quality_auto/04ee8b_1fcb4ce4bb7848078ebdb83cbbd71668~mv2.jpg', 2),
       (4, 'Cannondale CAAD13', 'Cuadro de aluminio avanzado y grupo Shimano 105.', 6200.00, 3,
        NOW(), 'https://tatoo.ws/cache/l/article/24761/107250.jpg', 2);

-- Urbanas
INSERT INTO product (id, name, description, price, stock, created_at, image_url, category_id)
VALUES (5, 'Giant Escape 3', 'Versátil con 7 velocidades, perfecta para traslados diarios.',
        2200.00, 12, NOW(), 'https://tatoo.ws/cache/l/article/26799/119543.jpg', 3),
       (6, 'Tern Link C8 (Plegable)', 'Compacta, ligera y fácil de transportar en ciudad.', 3200.00,
        6, NOW(), 'https://tatoo.ws/cache/l/article/26120/114602.jpg', 3);

-- Eléctricas
INSERT INTO product (id, name, description, price, stock, created_at, image_url, category_id)
VALUES (7, 'Cannondale Quick Neo',
        'Bicicleta eléctrica con motor Bosch y batería de larga duración.', 7800.00, 3, NOW(), 'https://www.monark.com.pe/static/monark-pe/uploads/products/images/e-bike-monark-e-motion-20-negro-verde.jpg',
        4),
       (8, 'Specialized Turbo Vado SL',
        'Eléctrica ligera con integración total de batería y motor.', 8900.00, 2, NOW(), 'https://tatoo.ws/cache/l/article/21621/89617.jpg', 4);

-- BMX
INSERT INTO product (id, name, description, price, stock, created_at, image_url, category_id)
VALUES (9, 'Mongoose Legion L100', 'Cuadro resistente de acero y ruedas de 20 pulgadas.', 1800.00,
        7, NOW(), 'https://tatoo.ws/cache/l/article/26797/119203.jpg', 5),
       (10, 'WeThePeople Trust BMX', 'Diseñada para freestyle con componentes de alta gama.',
        2500.00, 4, NOW(), 'https://static.wixstatic.com/media/04ee8b_09a33c4ee371470b8597755e0d5b4ce5~mv2.jpg/v1/fill/w_413,h_413,al_c,q_80,usm_0.66_1.00_0.01,enc_avif,quality_auto/04ee8b_09a33c4ee371470b8597755e0d5b4ce5~mv2.jpg', 5);

-- Gravel
INSERT INTO product (id, name, description, price, stock, created_at, image_url, category_id)
VALUES (11, 'Cannondale Topstone', 'Versátil con cuadro de aluminio y neumáticos todo terreno.',
        5200.00, 2, NOW(), 'https://oechsle.vteximg.com.br/arquivos/ids/2695175-1000-1000/1729196.jpg?v=637495297851500000', 6),
       (12, 'Specialized Diverge E5', 'Cuadro de aluminio ligero con horquilla de carbono.',
        6100.00, 3, NOW(), 'https://encrypted-tbn2.gstatic.com/shopping?q=tbn:ANd9GcRtnIiO0aSejG1S1C8xsCWM6Lbp43__VgWLD-WQG-0RmUMc_jtzkATEnr7GhCvXxTn6QFvxpPi9g7ilGq0SeomLgNJsxXFpNUp8Hy7ezk1ozzzWr08cxZNBX6X5zYggR-46qu60N7A&usqp=CAc', 6);
