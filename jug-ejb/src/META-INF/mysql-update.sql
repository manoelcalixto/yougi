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
# A field added to the event table to differentiate between an internal and an external event.
# Adding the message used to send emails to people who registered in an event.
# 27/11/2011
# Hildeberto Mendonca
# Version 0.26:0.5
alter table event modify start_time time null;

alter table event add address varchar(255) null;
alter table event add country char(3) null;
alter table event add province char(32) null;
alter table event add city char(32) null;
alter table event add latitude varchar(15) null;
alter table event add longitude varchar(15) null;

alter table event add constraint fk_country_event foreign key (country) references country(acronym) on delete set null;
alter table event add constraint fk_province_event foreign key (province) references province(id) on delete set null;
alter table event add constraint fk_city_event foreign key (city) references city(id) on delete set null;

alter table event add external tinyint(1) null default false;

insert into message_template (id, title, body) values
    ('KJDIEJKHFHSDJDUWJHAJSNFNFJHDJSLE', '[JUG] Confirmação de Comparecimento ao Evento', '<p>Oi <b>#{userAccount.firstName}</b>,</p><p>esta mensagem é só para informá-lo(a) que você acabou de confirmar seu comparecimento ao evento <b>#{event.name}</b>, que vai acontecer no(a) <b>#{event.venue}</b>, no dia <b>#{event.startDate}</b>, das <b>#{event.startTime}</b> até as <b>#{event.endTime}</b>.</p><p>Esperamos você lá!</p><p>Atenciosamente,</p><p><b>Coordenação do CEJUG</b></p>');

########################################################################
# Migrating data from the table contact to the table user_account and droping table contact.
# Adding a field to the table user_account to indicate whether the user was verified.
# 27/11/2011
# Hildeberto Mendonca
# Version 0.27:0.6
alter table user_account add website varchar(100) null;
alter table user_account add twitter varchar(30) null;
alter table user_account add country char(3) null;
alter table user_account add province char(32) null;
alter table user_account add city char(32) null;
alter table user_account add postal_code char(10) null;
alter table user_account add public_profile tinyint(1) null default false;
alter table user_account add mailing_list tinyint(1) null default false;
alter table user_account add news tinyint(1) null default false;
alter table user_account add general_offer tinyint(1) null default false;
alter table user_account add job_offer tinyint(1) null default false;
alter table user_account add event tinyint(1) null default false;
alter table user_account add sponsor tinyint(1) null default false;
alter table user_account add speaker tinyint(1) null default false;
alter table user_account add verified tinyint(1) null default false;

alter table user_account add constraint fk_country_user foreign key (country) references country(acronym) on delete set null;
alter table user_account add constraint fk_province_user foreign key (province) references province(id) on delete set null;
alter table user_account add constraint fk_city_user foreign key (city) references city(id) on delete set null;

update user_account, contact 
   set user_account.website = contact.website,
       user_account.twitter = contact.twitter,
       user_account.country = contact.country,
       user_account.province = contact.province,
       user_account.city = contact.city,
       user_account.postal_code = contact.postal_code
where user_account.id = contact.user;

update user_account, communication_privacy 
   set user_account.public_profile = communication_privacy.public_profile,
       user_account.mailing_list = communication_privacy.mailing_list,
       user_account.news = communication_privacy.news,
       user_account.general_offer = communication_privacy.general_offer,
       user_account.job_offer = communication_privacy.job_offer,
       user_account.event = communication_privacy.event,
       user_account.sponsor = communication_privacy.sponsor
where user_account.id = communication_privacy.user;

update user_account set verified = true;

alter table user_account drop column photo;
drop table contact;
drop table communication_privacy;

########################################################################
# Indicates which partners are sponsors of an event.
# 08/12/2011
# Hildeberto Mendonca
# Version 0.28:0.7
create table event_sponsor (
    id          char(32)      not null,
    event       char(32)      not null,
    partner     char(32)      not null,
    ammount     decimal(12,2)     null,
    description text              null
) engine = innodb;

alter table event_sponsor add constraint pk_event_sponsor primary key (id);
alter table event_sponsor add constraint fk_sponsor_event foreign key (event) references event(id) on delete cascade;
alter table event_sponsor add constraint fk_sponsor_partner foreign key (partner) references partner(id) on delete cascade;

########################################################################
# Creating table event_session and speaker.
# 08/12/2011
# Hildeberto Mendonca
# Version 0.29:0.8
create table event_session (
    id           char(32)     not null,
    event        char(32)     not null,
    title        varchar(255) not null,
    abstract     text             null,
    session_date date             null,
    start_time   time             null,
    end_time     time             null,
    room         varchar(30)      null
) engine = innodb;

alter table event_session add constraint pk_event_session primary key (id);
alter table event_session add constraint fk_event_session foreign key (event) references event(id) on delete cascade;

create table speaker (
    id       char(32)   not null,
    session  char(32)   not null,
    speaker  char(32)   not null,
    short_cv text           null
) engine = innodb;

alter table speaker add constraint pk_speaker primary key (id);
alter table speaker add constraint fk_session_speaker foreign key (session) references event_session(id) on delete cascade;
alter table speaker add constraint fk_user_speaker foreign key (speaker) references user_account(id) on delete cascade;