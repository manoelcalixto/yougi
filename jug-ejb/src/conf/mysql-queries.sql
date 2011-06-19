# Shows users and their correspondent subscriptions in the mailing_list
select ua.id, ua.email, mls.email_address, cp.mailing_list, mls.unsubscription_date
from user_account ua join mailing_list_subscription mls on ua.email = mls.email_address
                     join communication_privacy cp on ua.id = cp.user
where ua.deactivated = 0 and ua.confirmation_code is null;