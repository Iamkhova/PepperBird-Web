package com.thepepperbird.appengine;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.TwitterException;
import java.util.logging.Logger; 
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;


public class TwitterHandler
{
   public static final Logger log = Logger.getLogger(TwitterHandler.class.getName());
   
   /*
    * One time function to store API credentials into database
    */
   public void store2DB()
   {
     String key="twitter"; // set unique key
     
     // These items should be cleared prior to syncing codebase //
     String consumerKey = "PzN7jkeEkyCY5QxQhM4A";
     String consumerSecret = "etAYx4IlhlkKkEy6cJ2XP0P0TXzIqt1lLVpKewQ";
     String accessToken = "1071710311-h2xF6A0U5mA9qG7o8Xrq9UelAkRYPciOF83Pg2A";
     String accessTokenSecret = "0l2vsfLkxlafugpq4cQZuTEu5wzYtsASd9A5AnIwf0GVs";
     
     log.info ("Starting Datastore");    
     Entity db = new Entity("TwitterCreds", key);
     db.setProperty("consumerKey", consumerKey);
     db.setProperty("consumerSecret", consumerSecret);
     db.setProperty("accessToken", accessToken); 
     db.setProperty("accessTokenSecret", accessTokenSecret);
            
     DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
     datastore.put(db);
     log.info("Ending Datastore");
     
   }
}