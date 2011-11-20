########################################################################
# Adds two columns in the table city to represent its exact geographic coordinates.
# 23/10/2011
# Hildeberto Mendonca
# Version 0.22:0.1
alter table city add latitude varchar(15) null;
alter table city add longitude varchar(15) null;

########################################################################
# Adds two tables to represent partners and their respective representatives.
# 30/10/2011
# Hildeberto Mendonca
# Version 0.23:0.2
create table partner (
    id          char(32)     not null,
    name        varchar(32)  not null,
    description text             null,
    logo        varchar(100)     null,
    url         varchar(255)     null
) engine = innodb;

alter table partner add constraint pk_partner primary key (id);

create table representative (
    id           char(32)    not null,
    person       char(32)    not null,
    partner      char(32)    not null,
    phone        varchar(15)     null,
    position     varchar(20)     null
) engine = innodb;

alter table representative add constraint pk_representative primary key (id);
alter table representative add constraint fk_representative_person foreign key (person) references user_account(id) on delete cascade;
alter table representative add constraint fk_representative_partner foreign key (partner) references partner(id) on delete cascade;

########################################################################
# Adds two tables to represent events and attendees.
# 07/11/2011
# Hildeberto Mendonca
# Version 0.24:0.3
create table event (
    id                char(32)     not null,
    name              varchar(100) not null,
    venue             char(32)     not null,
    start_date        date         not null,
    start_time        time         not null,
    end_date          date         not null,
    end_time          time             null,
    description       text             null,
    short_description varchar(255)     null
) engine = innodb;

alter table event add constraint pk_event primary key (id);
alter table event add constraint fk_event_venue foreign key (venue) references partner(id) on delete cascade;

create table attendee (
    id                char(32)   not null,
    event             char(32)   not null,
    attendee          char(32)   not null,
    registration_date datetime   not null,
    attended          tinyint(1)     null
) engine = innodb;

alter table attendee add constraint pk_attendee primary key (id);
alter table attendee add constraint fk_attendee_event foreign key (event) references event(id) on delete cascade;
alter table attendee add constraint fk_attendee_user foreign key (attendee) references user_account(id) on delete cascade;

########################################################################
# Adds additional columns in partner.
# 20/11/2011
# Hildeberto Mendonca
# Version 0.25:0.4
alter table partner add address varchar(255) null;
alter table partner add city char(32) null;
alter table partner add province char(32) null;
alter table partner add country char(3) null;
alter table partner add postal_code char(10) null;

alter table partner add constraint fk_city_partner foreign key (city) references city(id) on delete set null;
alter table partner add constraint fk_province_partner foreign key (province) references province(id) on delete set null;
alter table partner add constraint fk_country_partner foreign key (country) references country(acronym) on delete set null;

########################################################################
# Adds additional columns in event to store the address and the geographical location.
# 09/11/2011
# Hildeberto Mendonca
# Version 0.26:0.5
alter table event add room varchar(50) null;
alter table event add address varchar(255) null;
alter table event add city char(32) null;
alter table event add province char(32) null;
alter table event add country char(3) null;
alter table event add latitude varchar(15) null;
alter table event add longitude varchar(15) null;

alter table event add constraint fk_city_event foreign key (city) references city(id) on delete set null;
alter table event add constraint fk_province_event foreign key (province) references province(id) on delete set null;
alter table event add constraint fk_country_event foreign key (country) references country(acronym) on delete set null;
