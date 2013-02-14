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
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import org.cejug.business.UserAccountBsn;
import org.cejug.entity.UserAccount;
import org.cejug.knowledge.business.TopicBean;
import org.cejug.knowledge.business.WebSourceBean;
import org.cejug.knowledge.entity.Article;
import org.cejug.knowledge.entity.Topic;
import org.cejug.knowledge.entity.WebSource;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@ManagedBean
@RequestScoped
public class WebSourceMBean {

    private static final Logger LOGGER = Logger.getLogger(WebSourceMBean.class.getName());

    @EJB
    private UserAccountBsn userAccountBsn;

    @EJB
    private WebSourceBean webSourceBean;

    private UserAccount provider;
    private WebSource webSource;

    private List<UserAccount> usersWithWebsite;

    @ManagedProperty(value="#{param.user}")
    private String userId;

    @ManagedProperty(value="#{unpublishedContentMBean}")
    private UnpublishedContentMBean unpublishedContentMBean;

    public UserAccount getProvider() {
        return this.provider;
    }

    public WebSource getWebSource() {
        return this.webSource;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<UserAccount> getUsersWithWebsite() {
        if(this.usersWithWebsite == null) {
            this.usersWithWebsite = userAccountBsn.findUserAccountsWithWebsite();
        }
        return this.usersWithWebsite;
    }

    public UnpublishedContentMBean getUnpublishedContentMBean() {
        return unpublishedContentMBean;
    }

    public void setUnpublishedContentMBean(UnpublishedContentMBean unpublishedContentMBean) {
        this.unpublishedContentMBean = unpublishedContentMBean;
    }

    public List<Article> getArticles() {
        return this.unpublishedContentMBean.getArticles();
    }

    @PostConstruct
    public void load() {
        if(this.userId != null && !this.userId.isEmpty()) {
            this.provider = userAccountBsn.findUserAccount(this.userId);
            this.webSource = webSourceBean.findWebSourceByProvider(this.provider);
            if(this.webSource == null) {
                this.webSource = new WebSource();
                this.webSource.setProvider(this.provider);
            }
            else {
                showFeedArticles();
            }
        }
    }

    public String reference() {
        webSourceBean.save(this.webSource);
        return "website";
    }

    public String undoReference() {
        webSourceBean.remove(this.webSource.getId());
        this.webSource.setId(null);
        return "website";
    }

    public String refreshUnpublishedContent() {
        try {
            this.unpublishedContentMBean.refreshArticles(loadFeedArticles());
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return "website";
    }

    public void showFeedArticles() {
        try {
            this.unpublishedContentMBean.setWebSource(this.webSource);
            if(this.unpublishedContentMBean.getArticles() == null) {
                this.unpublishedContentMBean.setArticles(loadFeedArticles());
            }
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    private List<Article> loadFeedArticles() throws IOException {
        List<Article> loadedArticles = null;

        String feedUrl = findWebsiteFeedURL();
        URL url  = new URL(feedUrl);

        try (XmlReader reader = new XmlReader(url)) {
            SyndFeed feed = new SyndFeedInput().build(reader);
            if(this.webSource != null) {
                this.webSource.setTitle(feed.getTitle());
            }
            loadedArticles = new ArrayList<>();
            Article article;
            for (Iterator i = feed.getEntries().iterator(); i.hasNext();) {
                SyndEntry entry = (SyndEntry) i.next();

                article = new Article();
                article.setTitle(entry.getTitle());
                article.setPermanentLink(entry.getLink());
                article.setAuthor(entry.getAuthor());
                article.setPublication(entry.getPublishedDate());
                article.setWebSource(this.webSource);
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

                loadedArticles.add(article);
            }
        } catch (IllegalArgumentException | FeedException ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
        }

        return loadedArticles;
    }

    private String findWebsiteFeedURL() {
        String feedUrl = null;
        String websiteContent = retrieveWebsiteContent();

        if(websiteContent == null) {
            return null;
        }

        Pattern urlPattern = Pattern.compile("https?://(www)?(\\.?\\w+)+(/\\w+)*");
        Matcher matcher = urlPattern.matcher(websiteContent);

        while (matcher.find()) {
            feedUrl = matcher.group();
            if(isFeedURL(feedUrl)) {
                if(this.webSource != null) {
                    this.webSource.setFeed(feedUrl);
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

    private String retrieveWebsiteContent() {
        StringBuilder content = null;
        String urlWebsite = this.provider.getWebsite();

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