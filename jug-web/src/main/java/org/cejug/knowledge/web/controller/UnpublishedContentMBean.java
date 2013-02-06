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
package org.cejug.knowledge.web.controller;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.cejug.knowledge.entity.Article;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@ManagedBean
@SessionScoped
public class UnpublishedContentMBean {

    private List<Article> articles;

    public List<Article> getArticles() {
        return articles;
    }

    public Article getArticle(String permanentLink) {
        if(articles == null || permanentLink == null) {
            return null;
        }

        Article article = null;
        for(Article art: articles) {
            if(art.getPermanentLink() != null && art.getPermanentLink().equals(permanentLink)) {
                article = art;
                break;
            }
        }

        return article;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }
}