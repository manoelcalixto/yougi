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
package org.cejug.yougi.knowledge.business;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.cejug.yougi.knowledge.entity.Article;
import org.cejug.yougi.knowledge.entity.WebSource;
import org.cejug.yougi.util.EntitySupport;

/**
 * Business logic dealing with articles from a web source.
 * @author Hildeberto Mendonca  - http://www.hildeberto.com
 */
@Stateless
@LocalBean
public class ArticleBean {

    private static final Logger LOGGER = Logger.getLogger(ArticleBean.class.getName());

    @PersistenceContext
    private EntityManager em;

    public Article findArticle(String id) {
        return em.find(Article.class, id);
    }

    public List<Article> findPublishedArticles(WebSource webSource) {
            return em.createQuery("select a from Article a where a.webSource = :webSource order by a.title asc")
                                 .setParameter("webSource", webSource)
                                 .getResultList();
    }

    public void save(Article article) {
        if(EntitySupport.INSTANCE.isIdNotValid(article)) {
            article.setId(EntitySupport.INSTANCE.generateEntityId());
            em.persist(article);
        }
        else {
            em.merge(article);
        }
    }

    public void remove(String id) {
        Article article = em.find(Article.class, id);
        if(article != null) {
            em.remove(article);
        }
    }

    public List<Article> findUnpublishedArticles(WebSource webSource) {
        List<Article> unpublishedArticles = null;

        String feedUrl = findWebsiteFeedURL(webSource);

        try {
            URL url  = new URL(feedUrl);
            XmlReader reader = new XmlReader(url);
            SyndFeed feed = new SyndFeedInput().build(reader);
            if(webSource != null) {
                webSource.setTitle(feed.getTitle());
            }
            unpublishedArticles = new ArrayList<>();
            Article article;
            for (Iterator i = feed.getEntries().iterator(); i.hasNext();) {
                SyndEntry entry = (SyndEntry) i.next();

                article = new Article();
                article.setTitle(entry.getTitle());
                article.setPermanentLink(entry.getLink());
                article.setAuthor(entry.getAuthor());
                article.setPublication(entry.getPublishedDate());
                article.setWebSource(webSource);
                if(entry.getDescription() != null) {
                    article.setSummary(entry.getDescription().getValue());
                }
                SyndContent syndContent;
                StringBuilder content = new StringBuilder();
                for(int j = 0;j < entry.getContents().size();j++) {
                    syndContent = (SyndContent) entry.getContents().get(j);
                    content.append(syndContent.getValue());
                }
                article.setContent(content.toString());

                unpublishedArticles.add(article);
            }
        } catch (IllegalArgumentException | FeedException | IOException ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
        }

        // Remove from the list of unpublished articles the ones that are already published.
        List<Article> publishedArticles = findPublishedArticles(webSource);
        for(Article publishedArticle: publishedArticles) {
            unpublishedArticles.remove(publishedArticle);
        }

        return unpublishedArticles;
    }

    private String findWebsiteFeedURL(WebSource webSource) {
        String feedUrl = null;
        String websiteContent = retrieveWebsiteContent(webSource);

        if(websiteContent == null) {
            return null;
        }

        Pattern urlPattern = Pattern.compile("https?://(www)?(\\.?\\w+)+(/\\w+)*");
        Matcher matcher = urlPattern.matcher(websiteContent);

        while (matcher.find()) {
            feedUrl = matcher.group();
            if(isFeedURL(feedUrl)) {
                if(webSource != null) {
                    webSource.setFeed(feedUrl);
                    LOGGER.log(Level.INFO, "Feed: {0}", feedUrl);
                }
                break;
            }
        }

        return feedUrl;
    }

    private boolean isFeedURL(String url) {
        if(url.contains("feed")) {
            return true;
        }
        return false;
    }

    private String retrieveWebsiteContent(WebSource webSource) {
        StringBuilder content = null;
        String urlWebsite = webSource.getProvider().getWebsite();

        if(!urlWebsite.contains("http")) {
            urlWebsite = "http://" + urlWebsite;
        }

        if(urlWebsite != null) {
            try {
                URL url = new URL(urlWebsite);
                BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
                String line = "";
                content = new StringBuilder();
                while(null != (line = br.readLine())) {
                    content.append(line);
                }
            }
            catch (IOException ex) {
                LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
        return content != null ? content.toString() : null;
    }
}