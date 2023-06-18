CREATE TABLE IF NOT EXISTS product
(
    product_id         INT          NOT NULL PRIMARY KEY AUTO_INCREMENT,
    product_name       VARCHAR(128) NOT NULL,
    category           VARCHAR(32)  NOT NULL,
    image_url          VARCHAR(256) NOT NULL,
    price              DECIMAL      NOT NULL,
    stock              INT          NOT NULL,
    description        VARCHAR(1024),
    created_date       TIMESTAMP    NOT NULL,
    last_modified_date TIMESTAMP    NOT NULL
);

CREATE TABLE IF NOT EXISTS `user`
(
    user_id            INT          NOT NULL PRIMARY KEY AUTO_INCREMENT,
    email              VARCHAR(256) NOT NULL UNIQUE,
    password           VARCHAR(256) NOT NULL,
    role               VARCHAR(256) NOT NULL,
    created_date       TIMESTAMP    NOT NULL,
    last_modified_date TIMESTAMP    NOT NULL
);

CREATE TABLE IF NOT EXISTS `order`
(
    order_id           INT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    user_id            INT       NOT NULL,
    total_amount       DECIMAL   NOT NULL, -- 訂單總花費
    created_date       TIMESTAMP NOT NULL,
    last_modified_date TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS order_item
(
    order_item_id INT     NOT NULL PRIMARY KEY AUTO_INCREMENT,
    order_id      INT     NOT NULL,
    product_id    INT     NOT NULL,
    quantity      INT     NOT NULL, -- 商品數量
    amount        DECIMAL NOT NULL  -- 商品花費
);

CREATE TABLE IF NOT EXISTS token
(
    token_id   INT          NOT NULL PRIMARY KEY AUTO_INCREMENT,
    jwt        VARCHAR(256) NOT NULL UNIQUE,
    token_type VARCHAR(32)  NOT NULL,
    expired    BOOLEAN      NOT NULL,
    revoked    BOOLEAN      NOT NULL,
    user_id    INT          NOT NULL
);