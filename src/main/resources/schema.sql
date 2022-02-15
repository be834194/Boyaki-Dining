DROP ALL OBJECTS;

CREATE TABLE IF NOT EXISTS account (
 username varchar(255) ,
 password varchar(255) not null,
 mail     varchar(255) not null,
 role     varchar(255) not null,
 primary key(username)
);

create table IF NOT EXISTS password_history(
 username    varchar(255) ,
 password    varchar(255) ,
 useday      datetime     ,
 primary key(username,useday)
)