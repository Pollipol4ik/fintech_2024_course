--liquibase formatted sql

--changeset Polina Kuptsova:create-locations-table
create sequence locations_pk_seq start 1 increment 1;

create table if not exists locations (
                                         id bigint not null primary key,
                                         name varchar(255) not null check (length(name) > 0),
                                         slug varchar(255) not null check (length(slug) > 0) unique
);

alter table locations alter column id set default nextval('locations_pk_seq');



--changeset Polina Kuptsova :create-events-table
create sequence events_pk_seq start 1 increment 1;

create table if not exists events (
                                      id bigint not null primary key,
                                      name varchar(255) not null check (length(name) > 0),
                                      date timestamp not null,
                                      place_id bigint not null,
                                      constraint fk_event_place foreign key (place_id) references locations(id) on delete cascade
);

alter table events alter column id set default nextval('events_pk_seq');


--rollback drop table locations;
--rollback drop table events;
