INSERT INTO categories(name)
VALUES ('Books');

INSERT INTO materials(deleted,name, owner_id,price,quantity,author,description)
VALUES
(FALSE,'The Great Gatsby', 1, 12.3, 10, 'F. Scott Fitzgerald', 'A tale of wealth, love, and the American Dream.'),
(FALSE,'To Kill a Mockingbird', 1, 10.3, 8, 'Harper Lee', 'A powerful story about racial injustice in the South.');

INSERT INTO material_category (material_id, category_id)
VALUES (
    (SELECT id FROM materials WHERE name='The Great Gatsby'),
    (SELECT id FROM categories WHERE name='Books')
);