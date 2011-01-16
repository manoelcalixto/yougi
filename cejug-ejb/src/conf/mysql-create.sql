create table country (
    acronym char(3)      not null,
    name    varchar(100) not null
) type = innodb;

alter table country add constraint pk_country primary key (acronym);

create table province (
    id      char(32)     not null,
    name    varchar(100) not null,
    country char(3)      not null
) type = innodb;

alter table province add constraint pk_province primary key (id);
alter table province add constraint fk_country_province foreign key (country) references country(acronym) on delete cascade;

create table city (
    id       char(32)     not null,
    name     varchar(100) not null,
    country  char(3)      not null,
    province char(32)         null,
    valid    tinyint(1)       null
) type = innodb;

alter table city add constraint pk_city primary key (id);
alter table city add constraint fk_country_city foreign key (country) references country(acronym) on delete cascade;
alter table city add constraint fk_province_city foreign key (province) references province(id) on delete set null;

create table user_account (
    id                  char(32)     not null,
    email               varchar(100) not null,
    username            varchar(100) not null,
    password            varchar(100) not null,
    first_name          varchar(50)  not null,
    last_name           varchar(50)  not null,
    gender              tinyint(1)   not null,
    birth_date          date             null,
    photo               varchar(100)     null,
    confirmation_code   varchar(32)      null,
    registration_date   timestamp        null,
    last_update         timestamp        null,
    deactivated         tinyint(1)       null default false,
    deactivation_date   timestamp        null,
    deactivation_reason varchar(255)     null
) type = innodb;

alter table user_account add constraint pk_user_account primary key (id);
create unique index idx_unique_user_email on user_account (email);
create unique index idx_unique_username on user_account (username);

create table contact (
    id                char(32)     not null,
    user              char(32)     not null,
    location          varchar(100)     null, # home, company, university, organization
    website           varchar(100)     null,
    city              char(32)         null,
    province          char(32)         null,
    country           char(3)          null,
    postal_code       char(10)         null,
    twitter           varchar(30)      null,
    main              tinyint(1)       null
) type = innodb;

alter table contact add constraint pk_contact primary key (id);
alter table contact add constraint fk_user_contact foreign key (user) references user_account(id) on delete cascade;
alter table contact add constraint fk_city_contact foreign key (city) references city(id) on delete set null;
alter table contact add constraint fk_province_contact foreign key (province) references province(id) on delete set null;
alter table contact add constraint fk_country_contact foreign key (country) references country(acronym) on delete set null;

create table communication_privacy (
    user           char(32)   not null,
    public_profile tinyint(1)     null,
    mailing_list   tinyint(1)     null,
    news           tinyint(1)     null,
    general_offer  tinyint(1)     null,
    job_offer      tinyint(1)     null,
    event          tinyint(1)     null
) type = innodb;

alter table communication_privacy add constraint pk_communication_privacy primary key (user);
alter table communication_privacy add constraint fk_user_privacy foreign key (user) references user_account(id) on delete cascade;

create table access_group (
    id           char(32)     not null,
    name         varchar(100) not null,
    description  text             null,
    user_default tinyint(1)       null
) type = innodb;

alter table access_group add constraint pk_access_group primary key (id);
create unique index idx_unique_group_name on access_group (name);

create table user_group (
    group_id   char(32)     not null,
    user_id    char(32)     not null,
    username   varchar(100) not null,
    group_name varchar(100) not null
) type = innodb;

alter table user_group add constraint pk_user_group primary key (group_id, user_id);
alter table user_group add constraint fk_group_user foreign key (group_id) references access_group(id) on delete cascade;
alter table user_group add constraint fk_user_group foreign key (user_id) references user_account(id) on delete cascade;

create table application_property (
    property_name  varchar(100) not null,
    property_value text             null
) type = innodb;

alter table application_property add constraint pk_application_property primary key (property_name);

create table message_template (
    id    char(32)     not null,
    title varchar(255) not null,
    body  text         not null
) type = innodb;

alter table message_template add constraint pk_message_template primary key (id);

create table event_venue (
    id          char(32)     not null,
    name        varchar(100) not null,
    address     varchar(255)     null,
    city        char(32)         null,
    province    char(32)         null,
    country     char(3)          null,
    postal_code char(10)         null,
    phone       varchar(20)      null,
    email       varchar(100)     null,
    website     varchar(100)     null
) type = innodb;

alter table event_venue add constraint pk_event_venue primary key (id);
alter table event_venue add constraint fk_city_venue foreign key (city) references city(id) on delete set null;
alter table event_venue add constraint fk_province_venue foreign key (province) references province(id) on delete set null;
alter table event_venue add constraint fk_country_venue foreign key (country) references country(acronym) on delete set null;

create table event (
    id          char(32)     not null,
    name        varchar(255) not null,
    start       datetime     not null,
    end         datetime     not null,
    venue       char(32)     not null,
    description text             null,
    hot_site    varchar(100)     null
) type = innodb;

alter table event add constraint pk_event primary key (id);
alter table event add constraint fk_event_venue foreign key (venue) references event_venue(id) on delete cascade;

create table event_attendee (
    id             char(32)     not null,
    event          char(32)     not null,
    attendee       char(32)     not null,
    attended       tinyint(1)       null
) type = innodb;

alter table event_attendee add constraint pk_event_attendee primary key (id);
alter table event_attendee add constraint fk_event_attendee foreign key (event) references event(id) on delete cascade;
alter table event_attendee add constraint fk_attendee_event foreign key (attendee) references user_account(id) on delete cascade;