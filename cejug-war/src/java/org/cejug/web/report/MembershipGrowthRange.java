/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cejug.web.report;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.cejug.entity.UserAccount;
import org.cejug.web.util.ResourceBundle;

/**
 *
 * @author Hildeberto Mendonca
 */
public class MembershipGrowthRange {

    private String rangeName;
    private Integer year;
    private Integer month;
    private Integer value;
    private Integer acumulated;

    public MembershipGrowthRange() {}

    private MembershipGrowthRange(String rangeName, Integer year, Integer month, Integer value, Integer acumulated) {
        this.rangeName = rangeName;
        this.year = year;
        this.month = month;
        this.value = value;
        this.acumulated = acumulated;
    }

    public static List<MembershipGrowthRange> generateSeries(List<UserAccount> userAccounts) {
        List<MembershipGrowthRange> membershipGrowthRanges = new ArrayList<MembershipGrowthRange>();

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

        Date registrationDate;
        Calendar date = Calendar.getInstance();
        MembershipGrowthRange membershipGrowthRange = null;
        int year = 0, month = 0, i = 0, acumulated = 0;
        boolean incremented = false;
        for(UserAccount userAccount: userAccounts) {
            registrationDate = userAccount.getRegistrationDate();
            date.setTime(registrationDate);

            if(year != date.get(Calendar.YEAR)) {
                year = date.get(Calendar.YEAR);
                incremented = true;
            }

            if(month != date.get(Calendar.MONTH)) {
                month = date.get(Calendar.MONTH);
                incremented = true;
            }

            if(incremented) {
                membershipGrowthRange = new MembershipGrowthRange(months[month] + ", " + year, year, month, 1, ++acumulated);
                membershipGrowthRanges.add(membershipGrowthRange);
                incremented = false;
            }
            else {
                acumulated += membershipGrowthRange.incrementValue();
            }
        }

        Date deactivationDate;
        boolean decremented = false;
        for(UserAccount userAccount: userAccounts) {
            if(!userAccount.getDeactivated())
                continue;

            deactivationDate = userAccount.getDeactivationDate();
            
            date.setTime(deactivationDate);
            year = date.get(Calendar.YEAR);
            month = date.get(Calendar.MONTH);

            for(MembershipGrowthRange mgr: membershipGrowthRanges) {
                if(mgr.isSamePeriod(year, month)) {
                    mgr.decrementValue();
                }
            }
        }

        return membershipGrowthRanges;
    }

    public String getRangeName() {
        return rangeName;
    }

    public void setRangeName(String rangeName) {
        this.rangeName = rangeName;
    }

    public boolean isSamePeriod(Integer year, Integer month) {
        if(this.year.equals(year) && this.month.equals(month))
            return true;
        else
            return false;
    }

    public Integer getValue() {
        return value;
    }

    public Integer incrementValue() {
        this.value++;
        this.acumulated++;
        return 1;
    }

    public void decrementValue() {
        this.value--;
        this.acumulated--;
    }

    public Integer getAcumulated() {
        return acumulated;
    }

    public void setAcumulated(Integer acumulated) {
        this.acumulated = acumulated;
    }
}