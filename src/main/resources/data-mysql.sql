-- user
INSERT IGNORE INTO `user` (email, password, created_date,
                           last_modified_date, role)
VALUES ('admin@ecommerce.com', '202cb962ac59075b964b07152d234b70', CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP, 'ADMIN');
