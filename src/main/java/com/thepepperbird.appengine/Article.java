package com.thepepperbird.appengine;

import java.util.logging.Logger;
import java.util.Date;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Unindex;
import com.googlecode.objectify.Key;

import java.security.GeneralSecurityException;
import java.io.IOException;

@Index
class SocialSync {
    @Index boolean isSynced;
    @Index Date    dateSynced;

}

@Entity
public class Article {

    public static final Logger log = Logger.getLogger(Article.class.getName());

    @Id
    String                     id;
    @Index
    private String             link;
    private String             title;
    private String             description;
    private Date               date;
    private SocialSync                 onBlogger = new SocialSync();
    private SocialSync                 onFacebook = new SocialSync();
    private SocialSync                 onGooglePlus = new SocialSync();
    private SocialSync                 onTwitter = new SocialSync();

    private Article() {
    };

    public Article(String link, String title, String description, Date date) {
        this.link = link;
        this.title = title;
        this.description = description;
        this.date = date;
        this.id = link;
        this.onBlogger.isSynced = false;
        this.onBlogger.dateSynced = date;
        this.onFacebook.isSynced = false;
        this.onFacebook.dateSynced = date;
        this.onGooglePlus.isSynced = false;
        this.onGooglePlus.dateSynced = date;
        this.onTwitter.isSynced = false;
        this.onTwitter.dateSynced = date;
       
    }

    //
    //TODO Set up Header
    //
    public void sync2Blogger(String _blogID) throws IOException, GeneralSecurityException {
        if (this.onBlogger.isSynced == false) {
            BlogHandler blog = new BlogHandler();

            // Post tp Blogger
            try {
                blog.postBlogByID(_blogID, this.title, this.description, this.link);
            } catch (Exception ex) {
                log.info("Post to blogger triggered exception" + ex);
            }

            //Update synced2blog flag
            this.onBlogger.isSynced = true;
            this.onBlogger.dateSynced = date;
        }else{ log.info("Post has already synced with blogger.");}
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
