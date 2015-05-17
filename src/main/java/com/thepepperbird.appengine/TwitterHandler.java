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
import twitter4j.conf.ConfigurationBuilder;
import java.io.IOException;

public class TwitterHandler
{
   public static final Logger log = Logger.getLogger(TwitterHandler.class.getName());
   
   
   
   /*
    * One time function to store API credentials into database
    */
   public void post2Twitter(String _content, String _url, String _hash)throws IOException, TwitterException
   {
   
         CredentialHandler secure = new CredentialHandler();
         secure.getCredsFromDS(secure.TWITTER_CREDS);
        //TODO Need to move these creds to database and outside of code.
         String consumerKey = secure.creds.consumerKey;
         String consumerSecret = secure.creds.consumerSecret;
         String accessToken = secure.creds.accessToken;
         String accessTokenSecret = secure.creds.accessTokenSecret;
         
        
         ConfigurationBuilder cb = new ConfigurationBuilder();
         cb.setDebugEnabled(true)
         .setOAuthConsumerKey(consumerKey)
         .setOAuthConsumerSecret(consumerSecret)
         .setOAuthAccessToken(accessToken)
         .setOAuthAccessTokenSecret(accessTokenSecret);
          TwitterFactory tf = new TwitterFactory(cb.build());
          Twitter twitter = tf.getInstance();
          
          StatusUpdate statusUpdate = new StatusUpdate(_content + " " + _url + " " + _hash);
          Status status = twitter.updateStatus(statusUpdate);


   }
}