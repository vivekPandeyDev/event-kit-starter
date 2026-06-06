create table if not exists outbox_event
(
    event_id uuid primary key,
    aggregate_id varchar(255) not null,
    topic varchar(255) not null,
    payload text not null,
    status varchar(50) not null,
    retry_count integer not null,
    created_at timestamp not null,
    published_at timestamp null
);

create index if not exists idx_outbox_status
on outbox_event(status, created_at);

create table if not exists processed_event
(
    event_id uuid not null,
    consumer varchar(255) not null,
    processed_at timestamp not null,
    primary key(event_id, consumer)
);