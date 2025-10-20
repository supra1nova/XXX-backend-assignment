-- users
create table tb_user (
    user_age INT DEFAULT 20 not null,
    created_at timestamp(6) with time zone not null,
    updated_at timestamp(6) with time zone,
    user_id bigint not null,
    user_name varchar(50) not null,
    primary key (user_id)
);


-- friends
create table tb_friend (
     approved_at timestamp(6) with time zone not null,
     from_user_id bigint not null,
     to_user_id bigint not null,
     user_id bigint not null,
     primary key (from_user_id, to_user_id, user_id)
);
create index idx_friends_user_id on tb_friend (user_id);
create index idx_friends_from_to on tb_friend (from_user_id, to_user_id);
create index idx_friends_to_from on tb_friend (to_user_id, from_user_id);
create index idx_friends_user_from on tb_friend (user_id, from_user_id);
create index idx_friends_user_to on tb_friend (user_id, to_user_id);
create index idx_friends_from_user on tb_friend (from_user_id);
create index idx_friends_to_user on tb_friend (to_user_id);


-- tb_friend_request
create table tb_friend_request (
    from_user_id bigint not null,
    requested_at timestamp(6) with time zone not null,
    to_user_id bigint not null,
    request_id varchar(255) not null,
    primary key (request_id)
);
create index idx_friend_request_to on tb_friend_request (to_user_id);
create index idx_friend_request_from_to on tb_friend_request (from_user_id, to_user_id);
create index idx_friend_request_to_from on tb_friend_request (to_user_id, from_user_id);


-- tb_friend_request_history
create table tb_friend_request_history (
     created_at timestamp(6) with time zone not null,
     from_user_id bigint not null,
     to_user_id bigint not null,
     history_id varchar(255) not null,
     request_id varchar(255) not null,
     status enum ('ACCEPTED','PENDING','REJECTED') not null,
     primary key (history_id)
);
create index idx_friend_request_history_from on tb_friend_request_history (from_user_id);
create index idx_friend_request_history_to on tb_friend_request_history (to_user_id);
create index idx_friend_request_history_from_to on tb_friend_request_history (from_user_id, to_user_id);
create index idx_friend_request_history_to_from on tb_friend_request_history (to_user_id, from_user_id);


-- seq
CREATE SEQUENCE IF NOT EXISTS user_seq START WITH 1 INCREMENT BY 1;
