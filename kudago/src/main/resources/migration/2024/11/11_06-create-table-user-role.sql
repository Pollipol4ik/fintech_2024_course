--liquibase formatted sql

--changeset PolinaKuptsova:create-role-table
create sequence if not exists role_pk_seq start 1 increment 1;

create table if not exists role
(
    id   bigint primary key default nextval('role_pk_seq'),
    name text not null check (length(name) > 0)
);

--changeset PolinaKuptsova:create-users-table
create sequence if not exists users_pk_seq start 1 increment 1;

create table if not exists users
(
    id         bigint primary key default nextval('users_pk_seq'),
    email      varchar(255) not null unique,
    password   varchar(255) not null,
    nickname   varchar(255),
    first_name varchar(255),
    last_name  varchar(255),
    role_id    bigint       not null,
    constraint fk_role foreign key (role_id) references role (id)
);

--rollback drop table users;
--rollback drop table role;
