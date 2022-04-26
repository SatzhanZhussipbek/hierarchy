create table hierarchy
(
    id            bigint unsigned auto_increment,
    category_name varchar(50)      not null,
    left_key      int              not null,
    right_key     int              not null,
    level         tinyint unsigned not null,
    primary key (id)
);