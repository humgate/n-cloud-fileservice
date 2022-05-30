/* DDL скрипт создания схемы (для справки). Схема БД в приложении создается через liquibase */
create table users
(
    id       serial primary key,
    login    varchar(64)  not null unique,
    password varchar(64)  not null,
    name     varchar(255) not null
);

/* Регулярное выражение для констрейнта на e-mail (login)
    ^ - начало строки
    [A-Za-z0-9._]+ - символ из диапазонов A-Z или a-z или 0-9 или символ . или символ _ один или более раз и
    @ - символ @ и
    [A-Za-z0-9.-]+ - тот же символ что описан в наборе выше один или более раз и
    [.] - символ . и
    [A-Za-z]+ - символ из диапазонов A-Z или a-z один или более раз
    $ - конец строки
*/
alter table users add constraint valid_email check (login ~* '^[A-Za-z0-9._]+[@][A-Za-z0-9.-]+[.][A-Za-z]+$');

create table files
(
    id      serial primary key,
    user_id integer references users (id) not null,
    filename    varchar(64) not null,
    data    bytea not null
);

alter table files add constraint uk_files unique (user_id, filename);

create table authorities
(
    id      serial primary key,
    user_id integer references users (id) not null,
    authority varchar(64)
);



