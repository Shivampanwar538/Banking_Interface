create table branch (
    branch_id BigInt primary key auto_increment,
    name varchar(255) not null,
    ifsc_code varchar(11) unique not null,
    address varchar(255) not null,
    city varchar(100) not null,
    state varchar(100) not null,
    postal_code varchar(20) not null
);
create table customer(
    customer_id BigInt primary key auto_increment,
    first_name varchar(50) not null ,
    last_name varchar(50) not  null,
    email varchar(100) unique not null,
    phone_number varchar(15) not null,
    date_of_birth date not null,
    address varchar(255) not null,
    created_at timestamp default current_timestamp NOT NULL
);
create table account(
    account_id BigInt primary key auto_increment,
    customer_id BigInt not null,
    branch_id BigInt not null,
    account_number varchar(20) unique not null,
    balance decimal(15,2) default 0.00 not null ,
    constraint chk_account_balance check ( balance >= 0),
    status varchar(20) default 'ACTIVE' not null,
    constraint chk_account_status check (status in ('ACTIVE', 'INACTIVE', 'CLOSED', 'FROZEN')),
    created_at timestamp default current_timestamp NOT NULL ,
    constraint fk_account_customer foreign key (customer_id) references customer(customer_id),
    constraint fk_account_branch foreign key (branch_id) references branch(branch_id)
);
create table bank_transaction(
    id BigInt primary key auto_increment,
    transaction_id varchar(50) unique not null,
    type varchar(20) not null,
    constraint chk_transaction_type check (type in ('DEPOSIT', 'WITHDRAWAL', 'TRANSFER')),
    channel varchar(20) not null,
    constraint chk_transaction_channel check (channel in ('ATM', 'INTERNET', 'BRANCH', 'MOBILE','UPI')),
    source_account varchar(20) ,
    destination_account varchar(20) ,
    amount decimal(15,2) not null ,
    status varchar(20) not null,
    constraint chk_transaction_status check (status in ('SUCCESS', 'REJECTED', 'FAILED')),
    created_at timestamp default current_timestamp NOT NULL
);
create table transaction_audit(
    id bigint primary key auto_increment,
    transaction_id varchar(50) not null,
    outcome varchar(20) not null,
    constraint chk_audit_outcome check (outcome in ('SUCCESS', 'REJECTED', 'FAILED','DUPLICATE')),
    thread_name varchar(100) not null,
    duration_ms bigint not null,
    constraint chk_audit_duration check (duration_ms >= 0),
    created_at timestamp default current_timestamp NOT NULL,
    constraint fk_audit_transaction foreign key (transaction_id) references bank_transaction(transaction_id)
);