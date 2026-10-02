CREATE TABLE CATEGORY (
    category_id int unique key not null auto_increment primary key,
    name        varchar(255) null
);

CREATE TABLE CUSTOMER (
    id       int unique key not null auto_increment primary key,
    address  varchar(255) null,
    email    varchar(255) null,
    password varchar(255) null,
    role     varchar(255) null,
    username varchar(255) null,
    UNIQUE (username)
);

CREATE TABLE PRODUCT (
    product_id  int unique key not null auto_increment primary key,
    description varchar(255) null,
    image       varchar(255) null,
    name        varchar(255) null,
    price       int null,
    quantity    int null,
    weight      int null,
    category_id int null,
    customer_id int null
);

CREATE INDEX FK7u438kvwr308xcwr4wbx36uiw ON PRODUCT (category_id);
CREATE INDEX FKt23apo8r9s2hse1dkt95ig0w5 ON PRODUCT (customer_id);

CREATE TABLE CART (
    id          int not null auto_increment primary key,
    customer_id int null
);

CREATE TABLE CART_PRODUCT (
    cart_id    int not null,
    product_id int not null,
    primary key (cart_id, product_id)
);
