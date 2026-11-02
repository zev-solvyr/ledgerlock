CREATE TABLE wallet(
    id UUID primary key default gen_random_uuid(),
    user_id VARCHAR(255) unique not null,
    balance NUMERIC(19,4) not null default 0.0000,
    currency varchar(3) not null default 'INR',
    updated_at timestamp with time zone default current_timestamp
);

create table transaction_history(
    id UUID primary key default gen_random_uuid(),
    source_wallet_id UUID references wallet(id),
    destination_wallet_id UUID references wallet(id),
    amount NUMERIC(19,4) not null,
    transaction_type varchar(50) not null,
    status varchar(50) not null,
    created_at timestamp with time zone default current_timestamp
);

create index idx_wallet_user_id on wallet(user_id);

create index idx_tx_source_wallet on transaction_history(source_wallet_id);
create index idx_dx_destination_wallet on transaction_history(destination_wallet_id);

