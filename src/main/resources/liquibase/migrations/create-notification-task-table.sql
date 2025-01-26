-- liquibase formatted sql

-- changeset author:1

create table if not exists notification_task (
    id bigserial primary key,
    chat_id bigint not null,
    message_text text not null,
    notification_date_time timestamp not null
);