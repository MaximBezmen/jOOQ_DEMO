create table role
(
    id        bigserial
        constraint pk_role primary key,
    role_name varchar(20)
);

comment on table role is 'Role of user.';

create table account
(
    id        bigserial
        constraint pk_account primary key,
    last_name varchar(30),
    firs_name varchar(40),
    email     varchar(100) not null unique,
    user_name varchar(255) not null unique,
    password  varchar(255) not null,
    role_id   bigint
        constraint fk_role_id
            references role
);

comment on table account is 'Account of user.';

create unique index account_user_name_uindex
    on account (user_name);


create table account_verification
(
    id          bigserial
        constraint pk_account_verification primary key,
    account_id  bigint
        constraint fk_account_id references account,
    expiry_at   timestamp with time zone not null,
    token       varchar(255)             not null,
    verified    boolean,
    verified_on timestamp with time zone
);

comment on table account_verification is 'Accounts verification data.';

insert into role (id, role_name) values (1, 'ADMIN');
insert into role (id, role_name) values (2, 'SELLER');

