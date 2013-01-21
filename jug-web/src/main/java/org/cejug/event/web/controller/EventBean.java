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
package org.cejug.event.web.controller;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.cejug.business.ApplicationPropertyBsn;
import org.cejug.business.MessengerBsn;
import org.cejug.business.UserAccountBsn;
import org.cejug.entity.ApplicationProperty;
import org.cejug.entity.Properties;
import org.cejug.entity.UserAccount;
import org.cejug.event.business.AttendeeBsn;
import org.cejug.event.business.EventBsn;
import org.cejug.event.entity.Attendee;
import org.cejug.event.entity.Event;
import org.cejug.partnership.business.PartnerBsn;
import org.cejug.partnership.entity.Partner;
import org.cejug.web.controller.LocationBean;
import org.cejug.web.controller.UserProfileBean;
import org.cejug.web.report.EventAttendeeCertificate;
import org.cejug.web.util.ResourceBundleHelper;
import org.cejug.web.util.WebTextUtils;
import org.primefaces.model.chart.PieChartModel;

/**
 * @author Hildeberto Mendonca  - http://www.hildeberto.com
 */
@ManagedBean
@RequestScoped
public class EventBean {

    static final Logger logger = Logger.getLogger("org.cejug.event.web.controller.EventBean");

    @EJB
    private EventBsn eventBsn;

    @EJB
    private AttendeeBsn attendeeBsn;

    @EJB
    private PartnerBsn partnerBsn;
    
    @EJB
    private UserAccountBsn userAccountBsn;

    @EJB
    private MessengerBsn messengerBsn;
    
    @EJB
    private ApplicationPropertyBsn applicationPropertyBsn;

    @ManagedProperty(value = "#{param.id}")
    private String id;

    @ManagedProperty(value = "#{locationBean}")
    private LocationBean locationBean;
    
    @ManagedProperty(value = "#{userProfileBean}")
    private UserProfileBean userProfileBean;

    private Event event;

    private Attendee attendee;

    private List<Event> events;

    private List<Event> commingEvents;

    private List<Partner> venues;

    private Long numberPeopleAttending;

    private Long numberPeopleAttended;
    
    private PieChartModel pieChartModel;

    private String selectedVenue;

    public EventBean() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocationBean getLocationBean() {
        return locationBean;
    }

    public void setLocationBean(LocationBean locationBean) {
        this.locationBean = locationBean;
    }

    public UserProfileBean getUserProfileBean() {
        return userProfileBean;
    }

    public void setUserProfileBean(UserProfileBean userProfileBean) {
        this.userProfileBean = userProfileBean;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Attendee getAttendee() {
        return attendee;
    }

    public void setAttendee(Attendee attendee) {
        this.attendee = attendee;
    }

    public String getSelectedVenue() {
        return selectedVenue;
    }

    public void setSelectedVenue(String selectedVenue) {
        this.selectedVenue = selectedVenue;
        
        Partner venue = partnerBsn.findPartner(selectedVenue);
        
        if (this.event.getAddress() == null && venue.getAddress() != null) {
            this.event.setAddress(venue.getAddress());
        }
        if (this.event.getCountry() == null && venue.getCountry() != null) {
            this.locationBean.setSelectedCountry(venue.getCountry().getAcronym());
        }
        if (this.event.getProvince() == null && venue.getProvince() != null) {
            this.locationBean.setSelectedProvince(venue.getProvince().getId());
        }
        if (this.event.getCity() == null && venue.getCity() != null) {
            this.locationBean.setSelectedCity(venue.getCity().getId());
        }
    }
    
    /**
     * @return true if the event ocurred on the day before today.
     */
    public Boolean getHappened() {
        TimeZone tz = TimeZone.getTimeZone(userProfileBean.getTimeZone());
        Calendar today = Calendar.getInstance(tz);
        
        if(this.event.getStartDate().before(today.getTime()))
            return true;
        
        return false;
    }

    /**
     * @return true if the member has the intention to attend the event. It does
     * not mean that s(he) actually attended it.
     */
    public Boolean getIsAttending() {
        if (attendee != null) {
            return true;
        }
        return false;
    }
    
    /**
     * @return true if the member actually attended the event.
     */
    public Boolean getAttended() {
        if(attendee != null) {
            return attendee.getAttended();
        }
        return Boolean.FALSE;
    }

    public List<Event> getEvents() {
        if (events == null) {
            events = eventBsn.findEvents();
        }
        return events;
    }

    public List<Event> getCommingEvents() {
        if (commingEvents == null) {
            commingEvents = eventBsn.findCommingEvents();
        }
        return commingEvents;
    }

    public List<Partner> getVenues() {
        if (venues == null) {
            venues = partnerBsn.findPartners();
        }
        return venues;
    }

    public Long getNumberPeopleAttending() {
        return numberPeopleAttending;
    }

    public void setNumberPeopleAttending(Long numberPeopleAttending) {
        this.numberPeopleAttending = numberPeopleAttending;
    }

    public void setNumberPeopleAttended(Long numberPeopleAttended) {
        this.numberPeopleAttended = numberPeopleAttended;
    }

    public Long getNumberPeopleAttended() {
        return numberPeopleAttended;
    }
    
    public PieChartModel getAttendanceRateChartModel() {
        pieChartModel = new PieChartModel();
        pieChartModel.set("Registered", numberPeopleAttending);
        pieChartModel.set("Attended", numberPeopleAttended);
        return pieChartModel;
    }

    public String getFormattedEventDescription() {
        return WebTextUtils.convertLineBreakToHTMLParagraph(event.getDescription());
    }

    public String getFormattedEventDescription(String description) {
        return WebTextUtils.convertLineBreakToHTMLParagraph(description);
    }

    public String getFormattedStartDate() {
        return WebTextUtils.getFormattedDate(event.getStartDate());
    }

    public String getFormattedStartDate(Date startDate) {
        return WebTextUtils.getFormattedDate(startDate);
    }

    public String getFormattedEndDate() {
        return WebTextUtils.getFormattedDate(event.getEndDate());
    }

    public String getFormattedStartTime() {
        return WebTextUtils.getFormattedTime(event.getStartTime(), userProfileBean.getTimeZone());
    }

    public String getFormattedStartTime(Date startTime) {
        return WebTextUtils.getFormattedTime(startTime, userProfileBean.getTimeZone());
    }

    public String getFormattedEndTime() {
        return WebTextUtils.getFormattedTime(event.getEndTime(), userProfileBean.getTimeZone());
    }

    public String getFormattedEndTime(Date endTime) {
        return WebTextUtils.getFormattedTime(endTime, userProfileBean.getTimeZone());
    }

    public String getFormattedRegistrationDate() {
        if (this.attendee == null) {
            return "";
        }
        return WebTextUtils.getFormattedDate(this.attendee.getRegistrationDate());
    }

    @PostConstruct
    public void load() {
        if (id != null && !id.isEmpty()) {
            this.event = eventBsn.findEvent(id);
            this.selectedVenue = this.event.getVenue().getId();

            HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            String username = request.getRemoteUser();
            UserAccount person = userAccountBsn.findUserAccountByUsername(username);
            this.attendee = attendeeBsn.findAttendee(this.event, person);
            this.numberPeopleAttending = attendeeBsn.findNumberPeopleAttending(this.event);
            this.numberPeopleAttended = attendeeBsn.findNumberPeopleAttended(this.event);

            locationBean.initialize();

            if (this.event.getCountry() != null) {
                locationBean.setSelectedCountry(this.event.getCountry().getAcronym());
            } else {
                locationBean.setSelectedCountry(null);
            }

            if (this.event.getProvince() != null) {
                locationBean.setSelectedProvince(this.event.getProvince().getId());
            } else {
                locationBean.setSelectedProvince(null);
            }

            if (this.event.getCity() != null) {
                locationBean.setSelectedCity(this.event.getCity().getId());
            } else {
                locationBean.setSelectedCity(null);
            }
        } else {
            this.event = new Event();
        }
    }

    public String confirmAttendance() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String username = request.getRemoteUser();
        UserAccount person = userAccountBsn.findUserAccountByUsername(username);

        this.event = eventBsn.findEvent(event.getId());

        Attendee newAttendee = new Attendee();
        newAttendee.setEvent(this.event);
        newAttendee.setAttendee(person);
        newAttendee.setRegistrationDate(Calendar.getInstance().getTime());
        attendeeBsn.save(newAttendee);
        ResourceBundleHelper rb = new ResourceBundleHelper();
        messengerBsn.sendConfirmationEventAttendance(newAttendee.getAttendee(),
                newAttendee.getEvent(),
                rb.getMessage("formatDate"),
                rb.getMessage("formatTime"),
                userProfileBean.getTimeZone());
        return "events?faces-redirect=true";
    }

    public String cancelAttendance() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String username = request.getRemoteUser();
        UserAccount person = userAccountBsn.findUserAccountByUsername(username);

        this.event = eventBsn.findEvent(event.getId());

        Attendee existingAttendee = attendeeBsn.findAttendee(event, person);
        attendeeBsn.remove(existingAttendee.getId());

        return "events?faces-redirect=true";
    }
    
    public void getCertificate() {
        if(!this.attendee.getAttended())
            return;
        
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletResponse response = (HttpServletResponse)context.getExternalContext().getResponse();
        response.setContentType("application/pdf");
        response.setHeader("Content-disposition", "inline=filename=file.pdf");

        try {
            Document document = new Document(PageSize.A4.rotate());
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            PdfWriter writer = PdfWriter.getInstance(document, output);
            document.open();

            ApplicationProperty fileRepositoryPath = applicationPropertyBsn.findApplicationProperty(Properties.FILE_REPOSITORY_PATH);
            
            EventAttendeeCertificate eventAttendeeCertificate = new EventAttendeeCertificate(document);
            StringBuilder certificateTemplatePath = new StringBuilder();
            certificateTemplatePath.append(fileRepositoryPath.getPropertyValue());
            certificateTemplatePath.append("/");
            certificateTemplatePath.append(event.getCertificateTemplate());
            eventAttendeeCertificate.setCertificateTemplate(writer, certificateTemplatePath.toString());
            
            this.attendee.generateCertificateData();
            this.attendeeBsn.save(this.attendee);
            eventAttendeeCertificate.generateCertificate(this.attendee);

            document.close();

            response.getOutputStream().write(output.toByteArray());
            response.getOutputStream().flush();
            response.getOutputStream().close();
            context.responseComplete();
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        } catch (DocumentException de) {
            logger.log(Level.SEVERE, de.getMessage(), de);
        }
    }

    public String save() {
        Partner venue = partnerBsn.findPartner(selectedVenue);
        this.event.setVenue(venue);
        this.event.setCountry(this.locationBean.getCountry());
        this.event.setProvince(this.locationBean.getProvince());
        this.event.setCity(this.locationBean.getCity());

        eventBsn.save(this.event);
        return "events?faces-redirect=true";
    }

    public String remove() {
        eventBsn.remove(this.event.getId());
        return "events?faces-redirect=true";
    }
}