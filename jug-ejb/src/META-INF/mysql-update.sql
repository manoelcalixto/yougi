########################################################################
# Adds two columns in the table city to represent its exact geographic coordinates.
# 23/10/2011
# Hildeberto Mendonca
# Version 0.1
alter table city add latitude varchar(15) null;
alter table city add longitude varchar(15) null;

########################################################################
# Adds two tables to represent partners and their respective representatives.
# 30/10/2011
# Hildeberto Mendonca
# Version 0.2
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
