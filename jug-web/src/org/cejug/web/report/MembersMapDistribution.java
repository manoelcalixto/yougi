package org.cejug.web.report;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import org.cejug.business.LocationBsn;
import org.cejug.entity.City;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;

@ManagedBean
@RequestScoped
public class MembersMapDistribution implements Serializable {  
  
	private static final long serialVersionUID = 1L;
	
	private MapModel simpleModel;
	
	@EJB
	private LocationBsn locationBsn;

    public MembersMapDistribution() {  
        simpleModel = new DefaultMapModel();
    }
    
    @PostConstruct
    public void load() {
    	List<City> cities = locationBsn.findValidatedCities();
        LatLng coord;
        Double latitude = null;
        Double longitude = null;
        for(City city:cities) {
        	if(city.getLatitude() != null && !city.getLatitude().isEmpty())
        		latitude = Double.valueOf(city.getLatitude());
        	
        	if(city.getLongitude() != null && !city.getLongitude().isEmpty())
        		longitude = Double.valueOf(city.getLongitude());
        	
        	if(latitude != null && longitude != null) {
        		coord = new LatLng(latitude, longitude);
        		simpleModel.addOverlay(new Marker(coord, city.getName()));
        	}
        }
    }
  
    public MapModel getSimpleModel() {  
        return simpleModel;  
    }
}