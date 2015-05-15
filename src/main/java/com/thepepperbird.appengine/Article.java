package com.thepepperbird.appengine;

import java.util.logging.Logger; 
import java.util.Date;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

class SocialSync {
  boolean isSynced;
  Date dateSynced;
  
}

@Entity
public class Article {
  
  public static final Logger log = Logger.getLogger(Article.class.getName());
  
  @Id long id;
  String link;
  String title;
  String description;
  SocialSync onBlogger;
  SocialSync onFacebook;
  SocialSync onGooglePlus;
  SocialSync onTwitter;
  
  private Article() {};
  
  public Article(String link, String title, String description)
    {
   	 this.link = link;
       this.title = title;
       this.description = description;
   }
  
}



