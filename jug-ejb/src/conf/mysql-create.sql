create table country (
    acronym char(3)      not null,
    name    varchar(100) not null
) engine = innodb;

alter table country add constraint pk_country primary key (acronym);

create table province (
    id      char(32)     not null,
    name    varchar(100) not null,
    country char(3)      not null
) engine = innodb;

alter table province add constraint pk_province primary key (id);
alter table province add constraint fk_country_province foreign key (country) references country(acronym) on delete cascade;

create table city (
    id       char(32)     not null,
    name     varchar(100) not null,
    country  char(3)      not null,
    province char(32)         null,
    valid    tinyint(1)       null
) engine = innodb;

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
    deactivation_reason varchar(255)     null,
    deactivation_type   tinyint(1)       null  # 0 - administrative  1 - ownwill
) engine = innodb;

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
) engine = innodb;

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
) engine = innodb;

alter table communication_privacy add constraint pk_communication_privacy primary key (user);
alter table communication_privacy add constraint fk_user_privacy foreign key (user) references user_account(id) on delete cascade;

create table access_group (
    id           char(32)     not null,
    name         varchar(100) not null,
    description  text             null,
    user_default tinyint(1)       null
) engine = innodb;

alter table access_group add constraint pk_access_group primary key (id);
create unique index idx_unique_group_name on access_group (name);

create table user_group (
    group_id   char(32)     not null,
    user_id    char(32)     not null,
    username   varchar(100) not null,
    group_name varchar(100) not null
) engine = innodb;

alter table user_group add constraint pk_user_group primary key (group_id, user_id);
alter table user_group add constraint fk_group_user foreign key (group_id) references access_group(id) on delete cascade;
alter table user_group add constraint fk_user_group foreign key (user_id) references user_account(id) on delete cascade;

create table application_property (
    property_key   varchar(100) not null,
    property_value text             null
) engine = MyISAM;

alter table application_property add constraint pk_application_property primary key (property_key);

create table message_template (
    id    char(32)     not null,
    title varchar(255) not null,
    body  text         not null
) engine = MyISAM;

alter table message_template add constraint pk_message_template primary key (id);

insert into message_template (id, title, body) values
    ('03BD6F3ACE4C48BD8660411FC8673DB4', '[JUG] Registration Deactivated', '<p>Dear <b>#{userAccount.firstName}</b>,</p><p>We are very sorry to inform that we cannot keep you as a CEJUG member.</p><p>Reason: <i>#{userAccount.deactivationReason}</i></p><p>We kindly appologize for the inconvenience and we count on your understanding.</p><p>Best Regards,</p><p><b>JUG Leadership Team</b></p>'),
    ('0D6F96382D91454F8155A720F3326F1B', '[JUG Admin] A New Member Joint the Group', '<p>Dear JUG Leader,</p><p><b>#{userAccount.fullName}</b> joint the JUG at #{userAccount.registrationDate}.</p><p>Regards,</p><p><b>JUG Management</b></p>'),
    ('47DEE5C2E0E14F8BA4605F3126FBFAF4', '[JUG] Welcome to CEJUG', '<p>Hi <b>#{userAccount.firstName}</b>,</p><p>you are confirmed as a member of the JUG. Welcome to the <b><a href=''http://www.cejug.org''>JUG Community</a></b>!</p><p>Thank you!</p><p><b>JUG Leadership Team</b></p>'),
    ('67BE6BEBE45945D29109A8D6CD878344', '[JUG] Request for Password Change', '<p>Hi <b>#{userAccount.firstName}</b>,</p><p>you requested to change your JUG password. The authorization code to perform this operation is:</p><p>#{userAccount.confirmationCode}</p><p>Inform this code in the form that you just saw right after requesting the new password or just follow the link below to fill out the form authomatically:</p><p><a href=''http://#{serverAddress}/change_password.xhtml?cc=#{userAccount.confirmationCode}''>http://#{serverAddress}/change_password.xhtml?cc=#{userAccount.confirmationCode}</a></p><p>Thank you!<br/>\r\n\r\n<b>JUG Leadership Team</b></p>'),
    ('E3F122DCC87D42248872878412B34CEE', '[JUG] Email Confirmation', '<p>Hi <b>#{userAccount.firstName}</b>,</p><p>you seems to register yourself as a member of JUG. We would like to confirm your email address to be able to contact you when necessary. You just have to click on the link below to confirm your email:</p><p><a href=''http://#{serverAddress}/EmailConfirmation?code=#{userAccount.confirmationCode}''>http://#{serverAddress}/EmailConfirmation?code=#{userAccount.confirmationCode}</a></p><p>If the address above does not look like a link, please select, copy and paste it your web browser. If you do not registered on JUG and beleave that this message was sent by mistake, please ignore it and accept our apologes.</p><p>Best Regards,</p><p><b>JUG Leadership Team</b></p>'),
    ('IKWMAJSNDOE3F122DCC87D4224887287', '[JUG] Membership Deactivated', '<p>Hi <b>#{userAccount.firstName}</b>,</p><p>we just knew that you wanna leave us :( Thank you for all contributions you have made to the JUG community.</p><p>All the best,</p><p><b>JUG Leadership Team</b></p>'),
    ('0D6F96382IKEJSUIWOK5A720F3326F1B', '[JUG Admin] A Member Was Deactivated', '<p>Dear JUG Leader,</p><p><b>#{userAccount.fullName}</b> was deactivated from the JUG due to the following reason:</p><p><i>#{userAccount.deactivationReason}</i></p><p>Regards,</p><p><b>JUG Management</b></p>');

create table mailing_list (
    id            char(32)     not null,
    name          varchar(50)  not null,
    description   varchar(255)     null,
    email         varchar(100)     null,
    subscription  varchar(100)     null,
    unsubcription varchar(100)     null
) engine = innodb;

alter table mailing_list add constraint pk_mailing_list primary key (id);

create table mailing_list_subscription (
    id                  char(32) not null,
    user_account        char(32) not null,
    mailing_list        char(32) not null,
    subscription_date   date         null,
    unsubscription_date date         null
) engine = innodb;

alter table mailing_list_subscription add constraint pk_mailing_list_subscription primary key (id);
alter table mailing_list_subscription add constraint fk_subsciption_user foreign key (user_account) references user_account(id) on delete cascade;
alter table mailing_list_subscription add constraint fk_subscription_mailing_list foreign key (mailing_list) references mailing_list(id) on delete cascade;

create table mailing_list_message (
    id            char(32)     not null,
    mailing_list  char(32)     not null,
    subject       varchar(255) not null,
    body          text         not null,
    sender        char(32)     not null,
    when_received datetime     not null,
    reply_to      char(32)         null,
    message_type  char(2)          null, # q - question, a - answer, i - info, ri - request_more_info, ir - info_requested, s - solution
    answer_score  int(5)           null,
    published     tinyint(1)       null
) engine = innodb;

alter table mailing_list_message add constraint pk_mailing_list_message primary key (id);
alter table mailing_list_message add constraint fk_mailing_list_message foreign key (mailing_list) references mailing_list(id) on delete cascade;
alter table mailing_list_message add constraint fk_message_sender foreign key (sender) references user_account(id) on delete cascade;
alter table mailing_list_message add constraint fk_message_reply_to foreign key (reply_to) references mailing_list_message(id) on delete set null;

create table topic (
    id          char(32)     not null,
    name        varchar(50)  not null,
    description varchar(255)     null
) engine = innodb;

alter table topic add constraint pk_topic primary key (id);

create table topic_mailinglist_message (
    id                  char(32) not null,
    topic               char(32) not null,
    mailinglist_message char(32) not null
) engine = innodb;

alter table topic_mailinglist_message add constraint pk_topic_mailinglist_message primary key (id);
alter table topic_mailinglist_message add constraint fk_topic_mailinglist_message foreign key (mailinglist_message) references mailing_list_message(id) on delete cascade;
alter table topic_mailinglist_message add constraint fk_mailinglist_message_topic foreign key (topic) references topic(id) on delete cascade;

create table tag (
    tag        char(40)   not null,
    kind       tinyint(2) not null,
    entity     char(32)   not null
) engine = MyISAM;

alter table tag add constraint pk_tag primary key (tag, kind, entity);