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

import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.cejug.entity.UpdateHistory;
import org.cejug.entity.UpdateHistoryPK;
import org.cejug.exception.BusinessLogicException;

/**
 *
 * @author Hildeberto Mendonca  - http://www.hildeberto.com
 */
@Stateless
@LocalBean
public class UpdateHistoryBsn {

    @PersistenceContext
    private EntityManager em;
    
    public UpdateHistory findUpdateHistory(UpdateHistoryPK id) {
        if (id != null)
            return em.find(UpdateHistory.class, id);
        return null;
    }
    
    public List<UpdateHistory> findLastUpdate() {
        return em.createQuery("select uh from UpdateHistory uh where uh.releaseDate = (select max(uh.releaseDate) from UpdateHistory uh)")
                 .getResultList();
    }
    
    public void save(UpdateHistory updateHistory) {
        if(updateHistory == null)
            throw new IllegalArgumentException();
        
        if (updateHistory.getUpdateHistoryPK() == null || !updateHistory.getUpdateHistoryPK().isValid()) {
            throw new BusinessLogicException("Update history not saved. Its id is invalid.");
        }
        
        try {
            em.merge(updateHistory);
        }
        catch(IllegalArgumentException iae) {
            em.persist(updateHistory);
        }
    }

    public void remove(String id) {
        UpdateHistory updateHistory = em.find(UpdateHistory.class, id);
        if (updateHistory != null) {
            em.remove(updateHistory);
        }
    }
}