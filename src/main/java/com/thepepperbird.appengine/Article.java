package com.thepepperbird.appengine;

import java.util.logging.Logger;
import java.util.Date;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.Key;

class SocialSync {
    boolean isSynced;
    Date    dateSynced;

}

@Entity
public class Article {

    public static final Logger log = Logger.getLogger(Article.class.getName());

    @Id String                       id;
    @Index private String             link;
    private String             title;
    private String             description;
    private Date               date;
    SocialSync                 onBlogger;
    SocialSync                 onFacebook;
    SocialSync                 onGooglePlus;
    SocialSync                 onTwitter;

    private Article() {
    };

    public Article(String link, String title, String description, Date date) {
        this.link = link;
        this.title = title;
        this.description = description;
      this.date = date;
      this.id = link;
    }

    //GETTERS AND SETTERS

    public void setLink(String link) {
        this.link = link;
    }

    public String getLink() {
        return this.link;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }

    public void setDescription(String desc) {
        this.description = desc;
    }

    public String getDescription() {
        return this.description;
    }
}
