CREATE TABLE IF NOT EXISTS products(
   product_id INT PRIMARY KEY,
   title VARCHAR(200),
    description VARCHAR(500),
    price DECIMAL(10,2),
    discount DECIMAL(5,2),
    discounted_price DECIMAL(10,2)
    );