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

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import org.cejug.business.UserAccountBsn;
import org.cejug.entity.UserAccount;
import org.cejug.web.util.ResourceBundle;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartSeries;

/**
 * This class feeds the bar chart that shows the growth of the user group in a
 * monthly basis.
 * @author Hildeberto Mendonca
 */
@ManagedBean
@RequestScoped
public class MembershipGrowth {
    
    @EJB
    private UserAccountBsn userAccountBsn;
    
    private CartesianChartModel membershipGrowthModel; 

    public MembershipGrowth() {}

    public CartesianChartModel getMembershipGrowthModel() {
        return membershipGrowthModel;
    }

    @PostConstruct
    public void load() {
        membershipGrowthModel = new CartesianChartModel();
        
        ResourceBundle bundle = new ResourceBundle();
        String[] months = {bundle.getMessage("januaryShort"),
                            bundle.getMessage("februaryShort"),
                            bundle.getMessage("marchShort"),
                            bundle.getMessage("aprilShort"),
                            bundle.getMessage("mayShort"),
                            bundle.getMessage("juneShort"),
                            bundle.getMessage("julyShort"),
                            bundle.getMessage("augustShort"),
                            bundle.getMessage("septemberShort"),
                            bundle.getMessage("octoberShort"),
                            bundle.getMessage("novemberShort"),
                            bundle.getMessage("decemberShort")};
        
        Calendar today = Calendar.getInstance();
        today.set(Calendar.DATE, today.getActualMaximum(Calendar.DAY_OF_MONTH));
        today.set(Calendar.HOUR, today.getActualMaximum(Calendar.HOUR));
        today.set(Calendar.MINUTE, today.getActualMaximum(Calendar.MINUTE));
        Date to = today.getTime();
        
        Calendar before = today;
        before.add(Calendar.YEAR, -2);
        before.add(Calendar.MONTH, 1);
        before.set(Calendar.HOUR, 0);
        before.set(Calendar.MINUTE, 0);
        Date from = before.getTime();
        
        List<UserAccount> userAccounts = userAccountBsn.findConfirmedUserAccounts(from, to);
        
        Calendar registrationDate;
        Calendar deactivationDate;
        int[][] data = new int[2][12];
        int year = -1, month = 0, currentYear = 0;
        for(UserAccount userAccount: userAccounts) {
            registrationDate = Calendar.getInstance();
            registrationDate.setTime(userAccount.getRegistrationDate());
            if(currentYear == 0)
                currentYear = registrationDate.get(Calendar.YEAR);
            
            year = registrationDate.get(Calendar.YEAR) - currentYear;
            month = registrationDate.get(Calendar.MONTH);
            data[year][month] += 1;
            
            if(userAccount.getDeactivationDate() != null) {
                deactivationDate = Calendar.getInstance();
                deactivationDate.setTime(userAccount.getDeactivationDate());
                
                year = deactivationDate.get(Calendar.YEAR) - currentYear;
                month = deactivationDate.get(Calendar.MONTH);
                data[year][month] -= 1;
            }
        }
        
        ChartSeries annualSeries;
        for(int i = 0;i < 2;i++) {
            annualSeries = new ChartSeries();
            annualSeries.setLabel(String.valueOf(currentYear + i));
            for(int j = 0;j < 12;j++) {
                annualSeries.set(months[j], data[i][j]);
            }
            membershipGrowthModel.addSeries(annualSeries);
        }
    }
}