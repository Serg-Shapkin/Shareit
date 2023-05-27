DROP TABLE IF EXISTS users, items, bookings, comments, request;

create table if not exists users (
                                       user_id    bigint generated by default as identity not null,
                                       user_name  varchar(50) not null,
                                       user_email varchar(50) not null,
                                       constraint user_pk primary key (user_id),
                                       constraint email_uq unique (user_email)
  );

create table if not exists items (
                                     item_id          bigint generated by default as identity not null,
                                     item_name        varchar(50),
                                     item_description varchar(255),
                                     item_available   boolean,
                                     item_owner_id    bigint not null,
                                     item_request_id  bigint,
                                     constraint items_pk primary key (item_id),
                                     constraint items_users_fk foreign key (item_owner_id) references users (user_id) on delete cascade
);

create table if not exists bookings (
                                        booking_id bigint generated by default as identity not null,
                                        start_date timestamp without time zone,
                                        end_date   timestamp without time zone,
                                        item_id    bigint not null,
                                        booker_id  bigint not null,
                                        status     varchar(20),
                                        constraint booking_pk primary key (booking_id),
                                        constraint bookings_users_fk foreign key (booker_id) references users (user_id) on delete cascade,
                                        constraint bookings_items_fk foreign key (item_id) references items (item_id) on delete cascade
);

create table if not exists comments (
                                        comment_id bigint generated by default as identity not null,
                                        comment_text varchar(255) not null,
                                        item_id bigint not null,
                                        author_id bigint not null,
                                        created timestamp without time zone,
                                        constraint comments_pk primary key (comment_id),
                                        constraint comments_items foreign key (item_id) references items (item_id) on delete cascade,
                                        constraint comments_users foreign key (author_id) references users (user_id) on delete cascade
);

create table if not exists request (
                                       request_id bigint generated by default as identity not null,
                                       request_description varchar(200) not null,
                                       requestor_id bigint not null,
                                       request_created timestamp not null,
                                       constraint request_pk primary key (request_id),
                                       constraint request_user_fk foreign key (requestor_id) references users (user_id) on delete cascade
);