###############################################################################
# Core                                                                        #
###############################################################################

create table update_history (
    db_version        varchar(10) not null,
    app_version       varchar(10) not null,
    date_release      timestamp   not null default CURRENT_TIMESTAMP,
    db_release_notes  text            null,
    app_release_notes text            null
) engine = MyISAM;

alter table update_history add constraint pk_update_history primary key (db_version, app_version);

create table application_property (
    property_key   varchar(100) not null,
    property_value text             null
) engine = MyISAM;

alter table application_property add constraint pk_application_property primary key (property_key);

insert into application_property values ('timezone', 'UTC 0:00'),
                                        ('url', 'http://localhost:8080/jug'),
                                        ('sendEmails', 'false'),
                                        ('groupName', 'UG'),
                                        ('language', 'en'),
                                        ('emailServerType', 'pop3');

create table message_template (
    id    char(32)     not null,
    title varchar(255) not null,
    body  text         not null
) engine = MyISAM;

alter table message_template add constraint pk_message_template primary key (id);

insert into message_template (id, title, body) values
    ('03BD6F3ACE4C48BD8660411FC8673DB4', '[UG] Registration Deactivated', '<p>Dear <b>#{userAccount.firstName}</b>,</p><p>We are very sorry to inform that we cannot keep you as a UG member.</p><p>Reason: <i>#{userAccount.deactivationReason}</i></p><p>We kindly appologize for the inconvenience and we count on your understanding.</p><p>Best Regards,</p><p><b>UG Leadership Team</b></p>'),
    ('0D6F96382D91454F8155A720F3326F1B', '[UG Admin] A New Member Joint the Group', '<p>Dear UG Leader,</p><p><b>#{userAccount.fullName}</b> joint the UG at #{userAccount.registrationDate}.</p><p>Regards,</p><p><b>UG Management</b></p>'),
    ('47DEE5C2E0E14F8BA4605F3126FBFAF4', '[UG] Welcome to UG', '<p>Hi <b>#{userAccount.firstName}</b>,</p><p>you are confirmed as a member of the UG. Welcome to the <b><a href=''http://www.cejug.org''>UG Community</a></b>!</p><p>Thank you!</p><p><b>UG Leadership Team</b></p>'),
    ('67BE6BEBE45945D29109A8D6CD878344', '[UG] Request for Password Change', '<p>Hi <b>#{userAccount.firstName}</b>,</p><p>you requested to change your password. The authorization code to perform this operation is:</p><p>#{userAccount.confirmationCode}</p><p>Inform this code in the form that you saw right after requesting the new password or just follow the link below to fill out the form automatically:</p><p><a href=''http://#{serverAddress}/change_password.xhtml?cc=#{userAccount.confirmationCode}''>http://#{serverAddress}/change_password.xhtml?cc=#{userAccount.confirmationCode}</a></p><p>Thank you!<br/>\r\n\r\n<b>UG Leadership Team</b></p>'),
    ('KJZISKQBE45945D29109A8D6C92IZJ89', '[UG] Request for Email Change', '<p>Hi <b>#{userAccount.firstName}</b>,</p><p>you requested to change your email address from <i>#{userAccount.email}</i> to <i>#{userAccount.unverifiedEmail}</i>. The authorization code to perform this operation is:</p><p>#{userAccount.confirmationCode}</p><p>Inform this code in the form that you saw right after changing the email address or just follow the link below:</p><p><a href=''http://#{serverAddress}/change_email_confirmation.xhtml?cc=#{userAccount.confirmationCode}''>http://#{serverAddress}/change_email_confirmation.xhtml?cc=#{userAccount.confirmationCode}</a></p><p>Thank you!<br/>\r\n\r\n<b>UG Leadership Team</b></p>'),
    ('E3F122DCC87D42248872878412B34CEE', '[UG] Registration Confirmation', '<p>Hi <b>#{userAccount.firstName}</b>,</p><p>you seems to register yourself as a member of UG. We would like to confirm your email address to be able to contact you when necessary. You just have to click on the link below to confirm your email:</p><p><a href=''http://#{serverAddress}/EmailConfirmation?code=#{userAccount.confirmationCode}''>http://#{serverAddress}/EmailConfirmation?code=#{userAccount.confirmationCode}</a></p><p>If the address above does not look like a link, please select, copy and paste it your web browser. If you do not registered on UG and beleave that this message was sent by mistake, please ignore it and accept our apologes.</p><p>Best Regards,</p><p><b>UG Leadership Team</b></p>'),
    ('IKWMAJSNDOE3F122DCC87D4224887287', '[UG] Membership Deactivated', '<p>Hi <b>#{userAccount.firstName}</b>,</p><p>we just knew that you wanna leave us :( Thank you for all contributions you have made to the UG community.</p><p>All the best,</p><p><b>UG Leadership Team</b></p>'),
    ('0D6F96382IKEJSUIWOK5A720F3326F1B', '[UG Admin] A Member Was Deactivated', '<p>Dear UG Leader,</p><p><b>#{userAccount.fullName}</b> was deactivated from the UG due to the following reason:</p><p><i>#{userAccount.deactivationReason}</i></p><p>Regards,</p><p><b>UG Management</b></p>'),
    ('09JDIIE82O39IDIDOSJCHXUDJJXHCKP0', '[UG Admin] Group Assigment', '<p>Hi <b>#{userAccount.firstName}</b>,</p><p>You were assigned to the <b>#{accessGroup.name}</b> group. Changes on your rights may apply.</p><p>Regards,</p><p><b>UG Management</b></p> '),
    ('KJDIEJKHFHSDJDUWJHAJSNFNFJHDJSLE', '[UG] Event Attendance', '<p>Hi <b>#{userAccount.firstName}</b>,</p><p>you have confirmed your attendance in the event <b>#{event.name}</b> that will take place at <b>#{event.venue}</b>, on <b>#{event.startDate}</b>, from <b>#{event.startTime}</b> to <b>#{event.endTime}</b>.</p><p>We are looking forward to see you there!</p><p>Best Regards,</p><p><b>UG Leadership Team</b></p>');

insert into message_template (id, title, body) values
    ('03BD6F3ACE4C48BR8660411FC8673DB4', '[CEJUG] Cancelamento de Registro', '<p>Caro(a) <b>#{userAccount.firstName}</b>,</p>\r\n<p>sentimos muito em informar que não poderemos manter o seu registro como membro do CEJUG.</p>\r\n<p>Motivo: <i>#{userAccount.deactivationReason}</i></p>\r\n<p>Pedimos desculpas pelo transtorno e contamos com a vossa compreensão.</p>\r\n<p>Atenciosamente,</p>\r\n<p><b>Coordenação do CEJUG</b></p>'),
    ('0D6F96382D91454H8155A720F3326F1B', '[CEJUG Admin] Um novo membro cadastrou-se no grupo', '<p>Caro Coordenador do CEJUG,</p>\r\n<p><b>#{newMember.fullName}</b> registrou-se como novo membro do CEJUG em #{newMember.registrationDate}.</p>\r\n<p>Atenciosamente,</p>\r\n<p><b>Sistema de Administração do CEJUG</b></p>'),
    ('47DEE5C2E0E14F8WA4605F3126FBFAF4', '[CEJUG] Bem vindo ao CEJUG', '<p>Oi <b>#{userAccount.firstName}</b>,</p>\r\n<p>seu registro foi confirmado com sucesso. Seja bem vindo ao <b><a href=''http://www.cejug.org''>CEJUG</a></b>!</p>\r\n<p>Para tratar de assuntos administrativos, como se registrar ou sair das listas de discussão, patrocinar o grupo ou compartilhar notícias, favor entrar em contato através do email admin@cejug.org.</p>\r\n<p>Atenciosamente,</p>\r\n<p><b>Coordenação do CEJUG</b></p>'),
    ('67BE6BEBE45945DF9109A8D6CD878344', '[CEJUG] Mudança de Senha', '<p>Oi <b>#{userAccount.firstName}</b>,</p>\r\n<p>você solicitou a mudança da sua senha do CEJUG. O código de autorização para mudar sua senha é:</p>\r\n<p>#{userAccount.confirmationCode}</p>\r\n<p>Informe este código no formulário de mudança de senha ou siga o endereço abaixo para preencher o campo automaticamente:</p>\r\n<p><a href=''http://#{serverAddress}/change_password.xhtml?cc=#{userAccount.confirmationCode}''>http://#{serverAddress}/change_password.xhtml?cc=#{userAccount.confirmationCode}</a></p>\r\n<p>Atenciosamente,<br/>\r\n<b>Coordenação do CEJUG</b></p>'),
    ('KJZISKQBE45945D291H9A8D6C92IZJ89', '[CEJUG] Solicitação de mudança de email', '<p>Oi <b>#{userAccount.firstName}</b>,</p><p>você solicitou a mudança do seu endereço de email de <i>#{userAccount.email}</i> para <i>#{userAccount.unverifiedEmail}</i>. O código de autorização para realizar esta operação é:</p><p>#{userAccount.confirmationCode}</p><p>Informe este código no campo que apareceu logo após a mudança de email ou apenas siga o link abaixo:</p><p><a href=''http://#{serverAddress}/change_email_confirmation.xhtml?cc=#{userAccount.confirmationCode}''>http://#{serverAddress}/change_email_confirmation.xhtml?cc=#{userAccount.confirmationCode}</a></p><p>Obrigado!<br/>\r\n\r\n<b>Coordenação do CEJUG</b></p>'),
    ('E3F122DCC87D422488M2878412B34CEE', '[CEJUG] Confirmação de Email', '<p>Oi <b>#{userAccount.firstName}</b>,</p>\r\n<p>você parece ter se registrado como membro no CEJUG. Nós gostariamos de confirmar o seu endereço de email para podermos entrar em contato sempre que necessário. Você só precisa clicar no link abaixo para confirmar o seu registro no CEJUG:</p>\r\n<p><a href=''http://#{serverAddress}/EmailConfirmation?code=#{userAccount.confirmationCode}''>http://#{serverAddress}/EmailConfirmation?code=#{userAccount.confirmationCode}</a></p>\r\n<p>Se o endereço acima não aparecer como link no seu cliente de email, selecione, copie e cole o endereço no seu navegador web. Se você não se registrou no CEJUG e acredita se tratar de um engano, por favor ignore esta mensagem e aceite nossas desculpas.</p>\r\n<p>Atenciosamente,</p>\r\n<p><b>Coordenação do CEJUG</b></p>'),
    ('IKWMAJSNDOE3F122DCA87D4224887287', '[CEJUG] Afiliação ao CEJUG Desativada', '<p>Oi <b>#{userAccount.firstName}</b>,</p>\r\n<p>nós acabamos de saber que você vai nos deixar :( Muito obrigado por todas as contribuições que você fez ao CEJUG. Crescemos juntos durante o período em que você esteve conosco.</p>\r\n<p>Desejamos o melhor,</p>\r\n<p><b>Coordenação do CEJUG</b></p>'),
    ('0D6F96382IKEJSUIWO25A720F3326F1B', '[CEJUG Admin] Um Membro foi Desativado', '<p>Caro JUG Leader,</p>\r\n<p><b>#{userAccount.fullName}</b> foi desativado do grupo devido a seguinte razão:</p>\r\n<p><i>#{userAccount.deactivationReason}</i></p>\r\n<p>Atenciosamente,</p>\r\n<p><b>Sistema de Administração do CEJUG</b></p>'),
    ('09JDIIE82O39IDIDOS5CHXUDJJXHCKP0', '[CEJUG Admin] Você foi adicionado a um grupo do CEJUG', '<p>Oi <b>#{userAccount.firstName}</b>,</p><p>Você foi adicionado ao grupo <b>#{accessGroup.name}</b>. Mudanças nas suas permissões de acesso devem ocorrer.</p><p>Atenciosamente,</p><p><b>Sistema de Administração do CEJUG</b></p>'),
    ('KJDIEJKHFHSDJDUWJH7JSNFNFJHDJSLE', '[CEJUG] Confirmação de Comparecimento ao Evento', '<p>Oi <b>#{userAccount.firstName}</b>,</p><p>esta mensagem é só para informá-lo(a) que você acabou de confirmar seu comparecimento ao evento <b>#{event.name}</b>, que vai acontecer no(a) <b>#{event.venue}</b>, no dia <b>#{event.startDate}</b>, das <b>#{event.startTime}</b> até as <b>#{event.endTime}</b>.</p><p>Esperamos você lá!</p><p>Atenciosamente,</p><p><b>Coordenação do CEJUG</b></p>');


create table language (
    acronym varchar(5)  not null,
    name    varchar(30) not null
) engine innodb;

alter table language add constraint pk_language primary key (acronym);

insert into language values ('en', 'English');
insert into language values ('pt', 'Portugues');

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
    id        char(32)     not null,
    name      varchar(100) not null,
    country   char(3)      not null,
    province  char(32)         null,
    valid     tinyint(1)       null,
    latitude  varchar(15)      null,
    longitude varchar(15)      null,
    timezone  varchar(20)      null
) engine = innodb;

alter table city add constraint pk_city primary key (id);
alter table city add constraint fk_country_city foreign key (country) references country(acronym) on delete cascade;
alter table city add constraint fk_province_city foreign key (province) references province(id) on delete set null;

create table user_account (
    id                  char(32)     not null,
    first_name          varchar(50)  not null,
    last_name           varchar(50)  not null,
    gender              tinyint(1)   not null,
    email               varchar(100)     null,
    unverified_email    varchar(100)     null,
    birth_date          date             null,
    confirmation_code   varchar(32)      null,
    registration_date   timestamp        null,
    last_update         timestamp        null,
    deactivated         tinyint(1)       null default false,
    deactivation_date   timestamp        null,
    deactivation_reason varchar(255)     null,
    deactivation_type   tinyint(1)       null,  # 0 - administrative  1 - ownwill  2 - unregistered
    website             varchar(100)     null,
    twitter             varchar(30)      null,
    country             char(3)          null,
    province            char(32)         null,
    city                char(32)         null,
    postal_code         varchar(10)      null,
    timezone            varchar(20)      null,
    public_profile      tinyint(1)       null default false,
    mailing_list        tinyint(1)       null default false,
    news                tinyint(1)       null default false,
    general_offer       tinyint(1)       null default false,
    job_offer           tinyint(1)       null default false,
    event               tinyint(1)       null default false,
    sponsor             tinyint(1)       null default false,
    speaker             tinyint(1)       null default false,
    verified            tinyint(1)       null default false
) engine = innodb;

alter table user_account add constraint pk_user_account primary key (id);
create unique index idx_unique_user_email on user_account (email);
alter table user_account add constraint fk_country_user foreign key (country) references country(acronym) on delete set null;
alter table user_account add constraint fk_province_user foreign key (province) references province(id) on delete set null;
alter table user_account add constraint fk_city_user foreign key (city) references city(id) on delete set null;

create table historical_message (
    id           char(32)     not null,
    subject      varchar(255) not null,
    body         text         not null,
    recipient    char(32)     not null,
    message_sent tinyint(1)       null,
    date_sent    datetime         null
) engine = innodb;

alter table historical_message add constraint pk_historical_message primary key (id);
alter table historical_message add constraint fk_message_recipient foreign key (recipient) references user_account(id) on delete cascade;

###############################################################################
# Security                                                                   #
###############################################################################

create table authentication (
    username            varchar(100) not null,
    password            varchar(100) not null,
    user_account        char(32)     not null
) engine = innodb;

alter table authentication add constraint pk_authentication primary key (username);
alter table authentication add constraint fk_user_authentication foreign key (user_account) references user_account(id) on delete cascade;

create table access_group (
    id           char(32)     not null,
    name         varchar(100) not null,
    description  text             null,
    user_default tinyint(1)       null
) engine = innodb;

alter table access_group add constraint pk_access_group primary key (id);
create unique index idx_unique_group_name on access_group (name);

insert into access_group (id, name, description, user_default) values
    ('PQOWKSIFUSLEOSJFNMDKELSOEJDKNWJE', 'helpers', 'Helpers', 0),
    ('IKSJDKMSNDJUEIKWQJSHDNCMXKLOPIKJ', 'partners', 'Partners', 0);

create table user_group (
    group_id   char(32)     not null,
    user_id    char(32)     not null,
    username   varchar(100) not null,
    group_name varchar(100) not null
) engine = innodb;

alter table user_group add constraint pk_user_group primary key (group_id, user_id);
alter table user_group add constraint fk_group_user foreign key (group_id) references access_group(id) on delete cascade;
alter table user_group add constraint fk_user_group foreign key (user_id) references user_account(id) on delete cascade;

###############################################################################
# Knowledge                                                                   #
###############################################################################

create table mailing_list (
    id             char(32)     not null,
    name           varchar(50)  not null,
    description    varchar(255)     null,
    email          varchar(100)     null,
    subscription   varchar(100)     null,
    unsubscription varchar(100)     null
) engine = innodb;

alter table mailing_list add constraint pk_mailing_list primary key (id);

create table mailing_list_subscription (
    id                  char(32)     not null,
    mailing_list        char(32)     not null,
    email_address       varchar(100) not null,
    user_account        char(32)         null,
    subscription_date   date             null,
    unsubscription_date date             null
) engine = innodb;

alter table mailing_list_subscription add constraint pk_mailing_list_subscription primary key (id);
alter table mailing_list_subscription add constraint fk_subscription_mailing_list foreign key (mailing_list) references mailing_list(id) on delete cascade;
alter table mailing_list_subscription add constraint fk_subsciption_user foreign key (user_account) references user_account(id) on delete set null;

create table topic (
    name        varchar(50)  not null,
    label       varchar(50)  not null,
    description text             null,
    valid       tinyint(1)       null default false
) engine = MyISAM;

alter table topic add constraint pk_topic primary key (name);

create table mailing_list_message (
    id            char(32)     not null,
    mailing_list  char(32)     not null,
    subject       varchar(255) not null,
    body          text         not null,
    sender        char(32)         null,
    date_received datetime     not null,
    reply_to      char(32)         null,
    message_type  tinyint(2)       null,
    topics        varchar(255)     null,
    published     tinyint(1)       null
) engine = innodb;

alter table mailing_list_message add constraint pk_mailing_list_message primary key (id);
alter table mailing_list_message add constraint fk_mailing_list_message foreign key (mailing_list) references mailing_list(id) on delete cascade;
alter table mailing_list_message add constraint fk_mailing_list_sender foreign key (sender) references mailing_list_subscription (id) on delete set null;
alter table mailing_list_message add constraint fk_message_reply_to foreign key (reply_to) references mailing_list_message(id) on delete set null;

create table web_source (
    id          char(32)     not null,
    title       varchar(100) not null,
    feed        varchar(255) not null,
    provider    char(32)         null
) engine innodb;

alter table web_source add constraint pk_web_source primary key (id);
alter table web_source add constraint fk_provider_web_source foreign key (provider) references user_account (id) on delete set null;

create table article (
    id               char(32)     not null,
    title            varchar(255) not null,
    author           varchar(100) not null,
    web_source       char(32)     not null,
    content          text         not null,
    summary          text             null,
    perm_link        varchar(255)     null,
    topics           varchar(255)     null,
    publication      date             null,
    published        tinyint(1)       null
) engine innodb;

alter table article add constraint pk_article primary key (id);
alter table article add constraint fk_source_article foreign key (web_source) references web_source (id) on delete cascade;

###############################################################################
# Partnership                                                                 #
###############################################################################

create table partner (
    id          char(32)     not null,
    name        varchar(100) not null,
    description text             null,
    logo        varchar(100)     null,
    url         varchar(255)     null,
    address     varchar(255)     null,
    country     char(3)          null,
    province    char(32)         null,
    city        char(32)         null,
    postal_code char(10)         null
) engine = innodb;

alter table partner add constraint pk_partner primary key (id);
alter table partner add constraint fk_city_partner foreign key (city) references city(id) on delete set null;
alter table partner add constraint fk_province_partner foreign key (province) references province(id) on delete set null;
alter table partner add constraint fk_country_partner foreign key (country) references country(acronym) on delete set null;

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

###############################################################################
# Event                                                                       #
###############################################################################

create table event (
    id                   char(32)     not null,
    name                 varchar(100) not null,
    venue                char(32)     not null,
    start_date           date         not null,
    end_date             date         not null,
    start_time           time             null,
    end_time             time             null,
    description          text             null,
    short_description    varchar(255)     null,
    address              varchar(255)     null,
    country              char(3)          null,
    province             char(32)         null,
    city                 char(32)         null,
    latitude             varchar(15)      null,
    longitude            varchar(15)      null,
    external             tinyint(1)       null default false,
    certificate_template varchar(100)     null
) engine = innodb;

alter table event add constraint pk_event primary key (id);
alter table event add constraint fk_event_venue foreign key (venue) references partner(id) on delete cascade;
alter table event add constraint fk_country_event foreign key (country) references country(acronym) on delete set null;
alter table event add constraint fk_province_event foreign key (province) references province(id) on delete set null;
alter table event add constraint fk_city_event foreign key (city) references city(id) on delete set null;

create table event_sponsor (
    id          char(32)      not null,
    event       char(32)      not null,
    partner     char(32)      not null,
    amount      decimal(12,2)     null,
    description text              null
) engine = innodb;

alter table event_sponsor add constraint pk_event_sponsor primary key (id);
alter table event_sponsor add constraint fk_sponsor_event foreign key (event) references event(id) on delete cascade;
alter table event_sponsor add constraint fk_sponsor_partner foreign key (partner) references partner(id) on delete cascade;

create table attendee (
    id                   char(32)     not null,
    event                char(32)     not null,
    attendee             char(32)     not null,
    registration_date    datetime     not null,
    attended             tinyint(1)       null,
    certificate_fullname varchar(100)     null,
    certificate_event    varchar(100)     null,
    certificate_venue    varchar(100)     null,
    certificate_date     date             null,
    certificate_code     char(36)         null
) engine = innodb;

alter table attendee add constraint pk_attendee primary key (id);
alter table attendee add constraint fk_attendee_event foreign key (event) references event(id) on delete cascade;
alter table attendee add constraint fk_attendee_user foreign key (attendee) references user_account(id) on delete cascade;

create table event_session (
    id           char(32)     not null,
    event        char(32)     not null,
    title        varchar(255) not null,
    topics       varchar(255)     null,
    abstract     text             null,
    session_date date             null,
    start_time   time             null,
    end_time     time             null,
    room         varchar(30)      null
) engine = innodb;

alter table event_session add constraint pk_event_session primary key (id);
alter table event_session add constraint fk_event_session foreign key (event) references event (id) on delete cascade;

create table speaker (
    id           char(32) not null,
    event        char(32) not null,
    session      char(32) not null,
    user_account char(32) not null,
    short_cv     text         null
) engine = innodb;

alter table speaker add constraint pk_speaker primary key (id);
alter table speaker add constraint fk_event_speaker foreign key (event) references event(id) on delete cascade;
alter table speaker add constraint fk_session_speaker foreign key (session) references event_session(id) on delete cascade;
alter table speaker add constraint fk_user_speaker foreign key (user_account) references user_account(id) on delete cascade;
