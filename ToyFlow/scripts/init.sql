drop database if exists toyflow;
create database toyflow;
use toyflow;

-- static tables

drop table if exists `state_type`;
create table `state_type`(
    `id`    varchar(20) not null ,
    `name`  varchar(100) not null,
    primary key (`id`)
);

drop table if exists `activity_type`;
create table `activity_type`(
    `id`    varchar(20) not null ,
    `name`  varchar(100) not null ,
    primary key (`id`)
);

drop table if exists `action_type`;
create table `action_type`(
    `id`    varchar(20) not null ,
    `name`  varchar(100) not null,
    primary key (`id`)
);

drop table if exists `target_type`;
create table `target_type`(
    `id`    varchar(20) not null ,
    `name`  varchar(20) not null ,
    primary key (`id`)
);

insert into `state_type` (`id`,`name`) values (1,'start'),(2,'normal')
                                            ,(3,'completed'),(4,'denied'),(5,'cancelled');
insert into `action_type` values (1,'approve'),(2,'deny')
                               ,(3,'cancel'),(4,'restart'),(5,'resolve');
insert into `target_type` values (1,'requester'),(2,'stakeholder')
                               ,(3, 'group members'), (4, 'process admin');
-- process

drop table if exists `process` ;
create table `process`(
    `id`    varchar(20) not null,
    `name`  varchar(100) not null,
    primary key (`id`)
);

drop table if exists `user`;
create table `user`(
    `id`    varchar(20) not null ,
    `name`  varchar(100) not null ,
    `external_id` varchar(100) not null ,
    `description`   varchar(500) not null ,
    primary key (`id`)
);

drop table if exists `admin`;
create table `admin`(
    `id`    varchar(20) not null ,
    `user_id`   varchar(20) not null ,
    `process_id`    varchar(20) not null ,
    primary key (`id`) ,
    foreign key (`user_id`) references `user`(`id`) ,
    foreign key (`process_id`) references `process`(`id`)
);

-- topology structure

drop table if exists `state`;
create table `state`(
    `id`            varchar(20)     not null ,
    `name`          varchar(100)    not null ,
    `state_type_id` varchar(20)     not null ,
    `pro_id`        varchar(20)     not null,
    primary key (`id`),
    foreign key (`state_type_id`) references `state_type` (`id`),
    foreign key (`pro_id`) references `process` (`id`)
);

drop table if exists `transition`;
create table `transition`(
    `id`            varchar(20)     not null ,
    `curr_state`    varchar(20)     not null ,
    `next_state`    varchar(20)     not null,
    `process_id`    varchar(20)     not null,
    primary key (`id`),
    foreign key (`curr_state`) references `state`(`id`),
    foreign key (`next_state`) references `state`(`id`),
    foreign key (`process_id`) references `process`(`id`)
);

-- group and target

drop table if exists `group`;
create table `group`(
                        `id`    varchar(20) not null ,
                        `name`  varchar(100) not null ,
                        `process_id`    varchar(20) not null ,
                        primary key (`id`),
                        foreign key (`process_id`) references `process`(`id`)
);

drop table if exists `group_member`;
create table `group_member`(
                               `id`    varchar(20) not null ,
                               `group_id`  varchar(20) not null ,
                               `user_id`   varchar(20) not null ,
                               primary key (`id`),
                               foreign key (group_id) references `group`(`id`),
                               foreign key (`user_id`) references `user`(`id`)
);

-- flow push and effect

drop table if exists `action`;
create table `action`(
    `id`    varchar(20) not null ,
    `name`  varchar(100),
    `type_id`   varchar(20) not null ,
    `process_id`    varchar(20) not null ,
    `transition_id` varchar(20) not null ,
    `group_id` varchar(20) not null ,
    `target_type_id` varchar(20) not null ,
    primary key (`id`),
    foreign key (`process_id`) references `process`(`id`),
    foreign key (`transition_id`) references `transition`(`id`),
    foreign key (`type_id`) references `action_type`(`id`),
    foreign key (`group_id`) references `group`(`id`),
    foreign key (`target_type_id`) references `target_type`(`id`)
);

-- runtime instance

drop table if exists `request`;
create table `request`(
    `id`    varchar(20) not null ,
    `process_id`    varchar(20) not null ,
    `name`  varchar(100) not null ,
    `request_time`  datetime not null ,
    `requester_user_id` varchar(20) not null ,
    `current_state` varchar(20) not null ,
    primary key (`id`),
    foreign key (`process_id`) references `process`(`id`),
    foreign key (`requester_user_id`) references `user`(`id`),
    foreign key (`current_state`) references `state`(`id`)
);

drop table if exists `request_data`;
create table `request_data`(
    `id`    varchar(20) not null ,
    `request_id`    varchar(20) not null ,
    `key`   varchar(100) not null ,
    `val`   varchar(500) not null ,
    primary key (`id`),
    foreign key (`request_id`) references `request`(`id`)
);

drop table if exists `request_actions`;
create table `request_actions`(
    `id`    varchar(20) not null ,
    `request_id`    varchar(20) not null ,
    `transition_id` varchar(20) not null ,
    `action_id`     varchar(20) not null ,
    primary key (`id`),
    foreign key (`request_id`) references `request`(`id`),
    foreign key (`transition_id`) references `transition`(`id`),
    foreign key (`action_id`) references `action`(`id`)
);

drop table if exists `stakeholder`;
create table `stakeholder`(
    `id`    varchar(20) not null ,
    `request_id`    varchar(20) not null ,
    `user_id`   varchar(20) not null ,
    primary key (`id`),
    foreign key (`request_id`) references `request`(`id`),
    foreign key (`user_id`) references `user`(`id`)
);

-- ----------------------------------------------------------------
-- ---------------------- an example process ----------------------
-- ----------------------------------------------------------------

insert into `process` values (1, 'example flow');
insert into `user` values (1,'Jim-总监 1',1,'an example of director'),
                          (2,'Bob-客户经理 1',2,'an example of customer manager'),
                          (3,'Lily-分管副总经理 1', 3, 'an example of vice manager'),
                          (4,'John-事业部总经理 1',4,'example of general manager'),
                          (5,'Amy-事业部客户经理 1',5,'example of 事业部客户经理'),
                          (6,'admin-流程管理员 1',6,'流程管理员');
insert into `admin` values (1,6,1);
insert into `state` values (1,'开始',1,1),
                           (2,'申请新建机会',2,1),
                           (3,'分管副总审批',2,1),
                           (4,'营销总监审批',2,1),
                           (5,'取消',5,1),
                           (6,'成功',3,1);
insert into `transition` values (1,1,2,1),
                                (2,2,3,1),
                                (3,2,5,1),
                                (4,3,2,1),
                                (5,3,4,1),
                                (6,3,5,1),
                                (7,4,2,1),
                                (8,4,5,1),
                                (9,4,6,1);
insert into `group` values (1,'营销总监',1),
                           (2,'客户经理',1),
                           (3,'分管副总',1);
insert into `group_member` values (1,1,1),
                                  (2,2,2),
                                  (3,3,3);
insert into `action` values (1,'新建机会',1,1,1,2,2),
                            (2,'提交分管副总',1,1,2,2,2),
                            (3,'客户经理主动取消',3,1,3,2,2),
                            (4,'分管副总退回修改',2,1,4,3,2),
                            (5,'分管副总同意',1,1,5,3,2),
                            (6,'分管副总取消',3,1,6,3,2),
                            (7,'营销总监退回修改',2,1,7,1,2),
                            (8,'营销总监取消',3,1,8,1,2),
                            (9,'营销总监通过',1,1,9,1,2);
