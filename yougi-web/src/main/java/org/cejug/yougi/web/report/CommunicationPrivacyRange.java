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
package org.cejug.yougi.web.report;

import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import org.cejug.yougi.business.UserAccountBsn;
import org.cejug.yougi.entity.UserAccount;
import org.cejug.yougi.util.ResourceBundleHelper;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartSeries;

/**
 * This class feeds a column chart that shows members' preferences in terms of
 * privacy.
 * @author Hildeberto Mendonca  - http://www.hildeberto.com
 */
@ManagedBean
@RequestScoped
public class CommunicationPrivacyRange implements Serializable {

    @EJB
    private UserAccountBsn userAccountBsn;
    
    private CartesianChartModel communicationPrivacyModel; 
    
    public CommunicationPrivacyRange() {}
    
    public CartesianChartModel getCommunicationPrivacyModel() {
        return this.communicationPrivacyModel;
    }
    
    @PostConstruct
    public void load() {
        communicationPrivacyModel = new CartesianChartModel();
        
        Integer totalPublicProfile = 0, totalMailingList = 0, totalNews = 0,
                totalGeneralOffer = 0, totalJobOffer = 0, totalEvent = 0, 
                totalSponsor = 0, totalSpeaker = 0;
        
        List<UserAccount> userAccounts = userAccountBsn.findUserAccounts();
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
            
            if(userAccount.getSpeaker())
                totalSpeaker++;
        }
        
        ResourceBundleHelper bundle = new ResourceBundleHelper();
        ChartSeries communicarionPrivacyActive = new ChartSeries();
        communicarionPrivacyActive.setLabel(bundle.getMessage("active"));
        communicarionPrivacyActive.set(bundle.getMessage("publicProfile"), totalPublicProfile);
        communicarionPrivacyActive.set(bundle.getMessage("mailingList"), totalMailingList);
        communicarionPrivacyActive.set(bundle.getMessage("news"), totalNews);
        communicarionPrivacyActive.set(bundle.getMessage("generalOffer"), totalGeneralOffer);
        communicarionPrivacyActive.set(bundle.getMessage("jobOffer"), totalJobOffer);
        communicarionPrivacyActive.set(bundle.getMessage("event"), totalEvent);
        communicarionPrivacyActive.set(bundle.getMessage("sponsor"), totalSponsor);
        communicarionPrivacyActive.set(bundle.getMessage("speaker"), totalSpeaker);
        this.communicationPrivacyModel.addSeries(communicarionPrivacyActive);
        
        Integer ttl = userAccounts.size();
        ChartSeries communicarionPrivacyInactive = new ChartSeries();
        communicarionPrivacyInactive.setLabel(bundle.getMessage("inactive"));
        communicarionPrivacyInactive.set(bundle.getMessage("publicProfile"), ttl - totalPublicProfile);
        communicarionPrivacyInactive.set(bundle.getMessage("mailingList"), ttl - totalMailingList);
        communicarionPrivacyInactive.set(bundle.getMessage("news"), ttl - totalNews);
        communicarionPrivacyInactive.set(bundle.getMessage("generalOffer"), ttl - totalGeneralOffer);
        communicarionPrivacyInactive.set(bundle.getMessage("jobOffer"), ttl - totalJobOffer);
        communicarionPrivacyInactive.set(bundle.getMessage("event"), ttl - totalEvent);
        communicarionPrivacyInactive.set(bundle.getMessage("sponsor"), ttl - totalSponsor);
        communicarionPrivacyInactive.set(bundle.getMessage("speaker"), ttl - totalSpeaker);
        this.communicationPrivacyModel.addSeries(communicarionPrivacyInactive);
    }
}