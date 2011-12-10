/* Jug Management is a web application conceived to manage user groups or 
 * communities focused on a certain domain of knowledge, whose members are 
 * constantly sharing information and participating in social and educational 
 * events. Copyright (C) 2011 Ceara Java User Group - CEJUG.
 * 
 * This application is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published by the 
 * Free Software Foundation; either version 2.1 of the License, or (at your 
 * option) any later version.
 * 
 * This application is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public 
 * License for more details.
 * 
 * There is a full copy of the GNU Lesser General Public License along with 
 * this library. Look for the file license.txt at the root level. If you do not
 * find it, write to the Free Software Foundation, Inc., 59 Temple Place, 
 * Suite 330, Boston, MA 02111-1307 USA.
 * */
package org.cejug.web.report;

import java.util.ArrayList;
import java.util.List;
import org.cejug.entity.UserAccount;
import org.cejug.web.util.ResourceBundle;

/**
 * This class feeds a column chart that shows members' preferences in terms of
 * privacy.
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
                totalGeneralOffer = 0, totalJobOffer = 0, totalEvent = 0, totalSponsor = 0;

        for(UserAccount userAccount: userAccounts) {
            if(userAccount.getPublicProfile())
                totalPublicProfile++;

            if(userAccount.getMailingList())
                totalMailingList++;

            if(userAccount.getNews())
                totalNews++;

            if(userAccount.getGeneralOffer())
                totalGeneralOffer++;

            if(userAccount.getJobOffer())
                totalJobOffer++;

            if(userAccount.getEvent())
                totalEvent++;

            if(userAccount.getSponsor())
                totalSponsor++;
        }

        Integer ttl = userAccounts.size();
        ResourceBundle bundle = new ResourceBundle();
        communicationPrivacyRanges.add(new CommunicationPrivacyRange(bundle.getMessage("publicProfile"), 
                                                                     totalPublicProfile,
                                                                     ttl - totalPublicProfile));

        communicationPrivacyRanges.add(new CommunicationPrivacyRange(bundle.getMessage("mailingList"),
                                                                     totalMailingList,
                                                                     ttl - totalMailingList));

        communicationPrivacyRanges.add(new CommunicationPrivacyRange(bundle.getMessage("news"),
                                                                     totalNews,
                                                                     ttl - totalNews));

        communicationPrivacyRanges.add(new CommunicationPrivacyRange(bundle.getMessage("generalOffer"),
                                                                     totalGeneralOffer,
                                                                     ttl - totalGeneralOffer));

        communicationPrivacyRanges.add(new CommunicationPrivacyRange(bundle.getMessage("jobOffer"),
                                                                     totalJobOffer,
                                                                     ttl - totalJobOffer));

        communicationPrivacyRanges.add(new CommunicationPrivacyRange(bundle.getMessage("event"),
                                                                     totalEvent,
                                                                     ttl - totalEvent));

        communicationPrivacyRanges.add(new CommunicationPrivacyRange(bundle.getMessage("sponsor"),
                                                                     totalSponsor,
                                                                     ttl - totalSponsor));

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