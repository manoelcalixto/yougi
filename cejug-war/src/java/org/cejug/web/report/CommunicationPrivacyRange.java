package org.cejug.web.report;

import java.util.ArrayList;
import java.util.List;
import org.cejug.entity.UserAccount;
import org.cejug.web.util.ResourceBundle;

/**
 *
 * @author Hildeberto Mendonca
 */
public class CommunicationPrivacyRange {

    private String rangeName;
    private Integer value;
    private Integer total;

    public CommunicationPrivacyRange() {}

    private CommunicationPrivacyRange(String rangeName, Integer value, Integer total) {
        this.rangeName = rangeName;
        this.value = value;
        this.total = total;
    }

    public static List<CommunicationPrivacyRange> generateSeries(List<UserAccount> userAccounts) {
        List<CommunicationPrivacyRange> communicationPrivacyRanges = new ArrayList<CommunicationPrivacyRange>();
        
        Integer totalPublicProfile = 0, totalMailingList = 0, totalNews = 0,
                totalGeneralOffer = 0, totalJobOffer = 0, totalEvent = 0;

        for(UserAccount userAccount: userAccounts) {
            if(userAccount.getPublicProfile()) totalPublicProfile++;
            if(userAccount.getMailingList()) totalMailingList++;
            if(userAccount.getNews()) totalNews++;
            if(userAccount.getGeneralOffer()) totalGeneralOffer++;
            if(userAccount.getJobOffer()) totalJobOffer++;
            if(userAccount.getEvent()) totalEvent++;
        }

        Integer ttl = userAccounts.size();
        ResourceBundle bundle = new ResourceBundle();
        communicationPrivacyRanges.add(new CommunicationPrivacyRange(bundle.getMessage("publicProfile"), totalPublicProfile, ttl - totalPublicProfile));
        communicationPrivacyRanges.add(new CommunicationPrivacyRange(bundle.getMessage("mailingList"), totalMailingList, ttl - totalMailingList));
        communicationPrivacyRanges.add(new CommunicationPrivacyRange(bundle.getMessage("news"), totalNews, ttl - totalNews));
        communicationPrivacyRanges.add(new CommunicationPrivacyRange(bundle.getMessage("generalOffer"), totalGeneralOffer, ttl - totalGeneralOffer));
        communicationPrivacyRanges.add(new CommunicationPrivacyRange(bundle.getMessage("jobOffer"), totalJobOffer, ttl - totalJobOffer));
        communicationPrivacyRanges.add(new CommunicationPrivacyRange(bundle.getMessage("event"), totalEvent, ttl - totalEvent));
        return communicationPrivacyRanges;
    }

    public String getRangeName() {
        return rangeName;
    }

    public void setRangeName(String rangeName) {
        this.rangeName = rangeName;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}