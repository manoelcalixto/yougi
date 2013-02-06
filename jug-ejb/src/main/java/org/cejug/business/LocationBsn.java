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
package org.cejug.business;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.cejug.entity.City;
import org.cejug.entity.Country;
import org.cejug.entity.Province;
import org.cejug.util.EntitySupport;

/**
 * Manages data of countries, states or provinces and cities because these
 * three entities are strongly related and because they are too simple to
 * have an exclusive business class.
 * @author Hildeberto Mendonca  - http://www.hildeberto.com
 */
@Stateless
@LocalBean
public class LocationBsn {
    @PersistenceContext
    private EntityManager em;

    @EJB
    private UserAccountBsn userAccountBsn;

    public Country findCountry(String acronym) {
        if(acronym != null) {
            return em.find(Country.class, acronym);
        }
        else {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<Country> findCountries() {
        return em.createQuery("select c from Country c order by c.name asc")
                 .getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Country> findAssociatedCountries() {
        return em.createQuery("select distinct p.country from Province p order by p.country")
                 .getResultList();
    }

    public Province findProvince(String id) {
        return em.find(Province.class, id);
    }

    @SuppressWarnings("unchecked")
    public List<Province> findProvinces() {
        return em.createQuery("select p from Province p order by p.country.name, p.name asc")
                 .getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Province> findProvinces(Country country) {
        return em.createQuery("select p from Province p where p.country = :country order by p.name asc")
                 .setParameter("country", country)
                 .getResultList();
    }

    public City findCity(String id) {
        return em.find(City.class, id);
    }

    @SuppressWarnings("unchecked")
    public List<City> findCities() {
        return em.createQuery("select c from City c order by c.country.name, c.name asc")
                 .getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<City> findValidatedCities() {
        return em.createQuery("select c from City c where c.valid = :valid")
        		 .setParameter("valid", true)
                 .getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<City> findCities(Country country, Boolean includingInvalids) {
        if(includingInvalids) {
            return em.createQuery("select c from City c where c.country = :country order by c.name asc")
                 .setParameter("country", country)
                 .getResultList();
        }
        else {
            return em.createQuery("select c from City c where c.country = :country and c.valid = :valid order by c.name asc")
                 .setParameter("country", country)
                 .setParameter("valid", Boolean.TRUE)
                 .getResultList();
        }
    }

    @SuppressWarnings("unchecked")
    public List<City> findCities(Province province, Boolean includingInvalids) {
        if(includingInvalids) {
            return em.createQuery("select c from City c where c.province = :province order by c.name asc")
                 .setParameter("province", province)
                 .getResultList();
        }
        else {
            return em.createQuery("select c from City c where c.province = :province and c.valid = :valid order by c.name asc")
                 .setParameter("province", province)
                 .setParameter("valid", Boolean.TRUE)
                 .getResultList();
        }
    }

    @SuppressWarnings("unchecked")
    public List<City> findCitiesStartingWith(String initials) {
        return em.createQuery("select c from City c where c.name like '"+ initials +"%' order by c.name").getResultList();
    }

    /**
     * @param name The name of the city.
     * @return An instance of city or null if there is not city with the given name.
     */
    public City findCityByName(String name) {
        List<City> candidates = em.createQuery("select c from City c where c.name = :name")
                 .setParameter("name", name)
                 .getResultList();
        if(candidates != null && candidates.size() == 1) {
            return candidates.get(0);
        }
        
        return null;
    }

    /**
     * Returns a list of time zones according to UTC standard.
     */
    public List<String> getTimeZones() {
        String prefix = "UTC";
        String signal = " ";
        String minutes = ":00";
        List<String> timeZones = new ArrayList<>();
        for(int i = -12;i <= 14;i++) {
            if(i > 0) {
                signal = " +";
            }
            if(i != 0) {
                timeZones.add(prefix + signal + i + minutes);
            }
            else {
                timeZones.add(prefix);
            }
        }
        return timeZones;
    }

    public void saveCountry(Country country) {
        Country existing = em.find(Country.class, country.getAcronym());
        if(existing == null) {
            em.persist(country);
        }
        else {
            em.merge(country);
        }
    }

    public void removeCountry(String id) {
        Country country = em.find(Country.class, id);
        if(country != null) {
            em.remove(country);
        }
    }

    public void saveProvince(Province province) {
        if(EntitySupport.INSTANCE.isIdNotValid(province)) {
            province.setId(EntitySupport.INSTANCE.generateEntityId());
            em.persist(province);
        }
        else {
            em.merge(province);
        }
    }

    public void removeProvince(String id) {
        Province province = em.find(Province.class, id);
        if(province != null) {
            em.remove(province);
        }
    }

    public void saveCity(City city) {
        if(EntitySupport.INSTANCE.isIdNotValid(city)) {
            city.setId(EntitySupport.INSTANCE.generateEntityId());
            em.persist(city);
        }
        else {
            em.merge(city);
        }

        userAccountBsn.updateTimeZoneInhabitants(city);
    }

    public void removeCity(String id) {
        City city = em.find(City.class, id);
        if(city != null) {
            em.remove(city);
        }
    }
}