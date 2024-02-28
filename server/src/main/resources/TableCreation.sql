create table users
(
    id        bigint not null
        primary key,
    meta_info varchar(255),
    password  varchar(255),
    login     varchar(255)
);

create table groups
(
    id        bigint not null
        primary key,
    name      varchar(255),
    meta_info varchar(255),
    password  varchar(255),
    admin     bigint
            references users
);

create table chapters
(
    id         bigint not null
        primary key,
    name       varchar(255),
    meta_info  varchar(255),
    test_data  varchar(255),
    text       varchar(255),
    group_host bigint
            references groups
);

create table users_groups
(
    group_id bigint not null
            references groups,
    user_id  bigint not null
            references users,
    primary key (group_id, user_id)
);