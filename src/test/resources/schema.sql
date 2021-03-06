DROP ALL OBJECTS;

create table IF NOT EXISTS status_list(
 statusid int primary key,
 statusname varchar(255) not null
);
  
create table IF NOT EXISTS post_category(
 postid int primary key,
 postname varchar(255) not null
);

create table IF NOT EXISTS account (
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
 primary key(username,useday),
 CONSTRAINT fk_username_passwordhistory 
            foreign key(username) references account(username) on delete cascade
);

create table IF NOT EXISTS diary_record(
 username     varchar(255) not null,
 categoryid   int          not null,
 diaryday     date        not null,
 record1      varchar(255) ,
 record2      varchar(255) ,
 record3      varchar(255) ,
 imagename    varchar(300) ,
 memo         varchar(255) ,
 createat     datetime     ,
 updateat     datetime     ,
 primary key(username,categoryid,diaryday),
 CONSTRAINT fk_username_diaryrecord 
            foreign key(username) references account(username) on delete cascade
 );

 create table IF NOT EXISTS account_info(
  username varchar(255) not null,
  nickname varchar(255) unique,
  profile  varchar(255) ,
  status   int          ,
  gender   int          ,
  age      int          ,
  height   float        ,
  weight   float        ,
  createat     datetime ,
  updateat     datetime ,
  primary key(username),
  CONSTRAINT fk_username_accountinfo 
             foreign key(username) references account(username) on delete cascade,
  CONSTRAINT fk_status_accountinfo 
             foreign key(status) references status_list(statusid)
 );
 
 create table IF NOT EXISTS post(
  postid       bigint       auto_increment primary key,
  username     varchar(255) not null,
  nickname     varchar(255) ,
  content      varchar(255) not null,
  postcategory int          not null,
  createat     datetime,
  CONSTRAINT fk_username_post 
             foreign key(username) references account(username) on delete cascade,
  CONSTRAINT fk_nickname_post 
             foreign key(nickname) references account_info(nickname) on delete cascade on update cascade,
  CONSTRAINT fk_postcategory_post 
             foreign key(postcategory) references post_category(postid)
 );
 
 create table IF NOT EXISTS comment(
  commentid    bigint       auto_increment primary key,
  postid       bigint,
  username     varchar(255) not null,
  nickname     varchar(255) not null,
  content      varchar(255) not null,
  createat     datetime,
  CONSTRAINT fk_nickname_comment 
             foreign key(nickname) references account_info(nickname) on delete cascade on update cascade
 );
 
 create table IF NOT EXISTS likes(
  postid   bigint       ,
  username varchar(255) ,
  rate     int,
  primary key(postid,username),
  CONSTRAINT unique_username_likes 
             foreign key(username) references account(username) on delete cascade
 );
