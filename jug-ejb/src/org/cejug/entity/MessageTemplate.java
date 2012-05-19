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
package org.cejug.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.persistence.*;

/**
 * Message template with variables to be fulfilled with object attributes.
 *
 * @author Hildeberto Mendonca
 */
@Entity
@Table(name = "message_template")
public class MessageTemplate implements Serializable {
    
    private static final String VAR_PATTERN = "\\#\\{([a-z][a-zA-Z_0-9]*(\\.)?)+\\}";

    private static final long serialVersionUID = 1L;

    private String id;

    private String title;

    private String body;

    public MessageTemplate() {
    }

    public MessageTemplate(String id) {
        this.id = id;
    }

    @Id
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Column(nullable = false)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Column(nullable = false)
    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Transient
    public String getTruncatedBody() {
        if (this.body.length() < 200) {
            return this.body;
        } else {
            return this.body.substring(0, 200);
        }
    }
    
    public void replaceVariablesByValues(Map<String, Object> values) {
        Pattern pattern = Pattern.compile(VAR_PATTERN);
        List<String> variables = findVariables(pattern, this.getBody());
        Object value;
        for(String variable: variables) {
            variable = variable.substring(2, variable.length() - 1);
            value = values.get(variable);
            if(value != null)
                this.body = this.body.replace("#{" + variable + "}", values.get(variable).toString());
        }
    }
    
    private List<String> findVariables(Pattern pattern, CharSequence charSequence) {
        Matcher m = pattern.matcher(charSequence);
        List<String> matches = new ArrayList<String>();
        while (m.find()) {
            matches.add(m.group());
        }
        return matches;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MessageTemplate other = (MessageTemplate) obj;
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return this.title;
    }
}