package com.thepepperbird.appengine;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.PreparedQuery;
import java.util.logging.Logger; 
import java.net.URL; 
import javax.xml.parsers.DocumentBuilder; 
import javax.xml.parsers.DocumentBuilderFactory; 
import org.w3c.dom.CharacterData; 
import org.w3c.dom.Document; 
import org.w3c.dom.Element; 
import org.w3c.dom.Node; 
import org.w3c.dom.NodeList;
import java.io.IOException;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.Iterator; 
import java.util.List;
import com.google.gson.Gson;
import java.security.GeneralSecurityException;

public class CredentialHandler
{
   public static final Logger log = Logger.getLogger(SocialHandler.class.getName());
   public static final String BLOGGER_CREDS = "bloggerCreds";
   public static final String TWITTER_CREDS = "twitterCreds";
   public static final String FACEBOOK_CREDS = "facebookCreds";
   public static final String LIBERIA_NEWS_BLOG = "liberiaNewsBlog";
   public static final String LIBERIA_DEATH_ANNOUCEMENTS = "liberiaDeathBlog";
   
   public CredStore creds = new CredStore();
   
   
  class CredStore {
   String consumerKey;
   String consumerSecret;
   String accessToken;
   String accessTokenSecret;
   String apiSecret;
   String appID;
   String pageID;
   String blogID;
   String clientID;
   String clientSecret;
   String refreshToken;
   }
   
   public void storeBlogID(String _blogKey, String _blogID)
   {
     log.info ("Starting Datastore");
    
     Entity db = new Entity(_blogKey, _blogKey);
     db.setProperty("blogID", _blogID);
    
     DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
     datastore.put(db);
     log.info("Ending Datastore");    
   }
   
   public String getBlogID(String _blogKey)
   {
     log.info ("Starting Datastore");
     
     String blogID = "";
     
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
     
     // Use class Query to assemble a query
     Query q = new Query(_blogKey);
    
     // Use PreparedQuery interface to retrieve results
     PreparedQuery pq = datastore.prepare(q);

     log.info("Query Starting");

     for (Entity result : pq.asIterable()) {
       blogID = (String) result.getProperty("blogID");
     
       log.info("Secret Token Pulled");
     }// for Loop
     
     return blogID; 
   }
   
   
    /*
    * storeBloggerCreds (String _apiSecret, String _blogID)
    */
   public void storeBloggerCreds(String _clientID, String _clientSecret, String _refreshToken)
   {
     clean();
     
     creds.clientID = _clientID;
     creds.clientSecret = _clientSecret;
     creds.refreshToken = _refreshToken;

     storeCreds2DS(BLOGGER_CREDS);
   }  
   
   
   
   
    /*
    *  storeFacebookCreds (String _appID, String _pageID, String _consumerSecret, String _accessToken)
    */
   public void storeFacebookCreds(String _appID, String _pageID, String _consumerSecret, String _accessToken)
   {
     clean();
     
     creds.appID = _appID;
     creds.pageID = _pageID;
     creds.consumerSecret = _consumerSecret;
     creds.accessToken = _accessToken;
     
     storeCreds2DS(FACEBOOK_CREDS);
   }
    
     /*
    *  storeTwitterCreds (String _consumerKey, String _consumerSecret, String _accessToken, String _accessTokenSecret)
    */
   public void storeTwitterCreds(String _consumerKey, String _consumerSecret, String _accessToken, String _accessTokenSecret)
   {
     clean();
     
     creds.consumerKey = _consumerKey;
     creds.consumerSecret = _consumerSecret;
     creds.accessToken = _accessToken;
     creds.accessTokenSecret = _accessTokenSecret;
     
     storeCreds2DS(TWITTER_CREDS);
   }

   public CredStore getCredsFromDS(String _socialType)
   {
     log.info ("Starting Datastore");
      // Read Datastore
     DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
     
     // Use class Query to assemble a query
     Query q = new Query(_socialType);
    
     // Use PreparedQuery interface to retrieve results
     PreparedQuery pq = datastore.prepare(q);

     log.info("Query Starting");

     for (Entity result : pq.asIterable()) {
       creds.consumerKey= (String) result.getProperty("consumerKey");
       creds.consumerSecret =(String) result.getProperty("consumerSecret");
       creds.accessToken = (String) result.getProperty("accessToken");
       creds.accessTokenSecret =(String) result.getProperty("accessTokenSecret");
       creds.apiSecret =(String) result.getProperty("apiSecret");
       creds.appID =(String) result.getProperty("appID");
       creds.pageID =(String) result.getProperty("pageID");
       creds.blogID =(String) result.getProperty("blogID");
       creds.clientID =(String) result.getProperty("clientID");
       creds.clientSecret = (String) result.getProperty("clientSecret");
       creds.refreshToken = (String) result.getProperty("refreshToken");
       log.info("Secret Token Pulled");
    }// for Loop
     
     return creds;
     
   }
   
   private void storeCreds2DS(String _socialType)
   {
     String key= _socialType; // set unique key
     
     log.info ("Starting Datastore");
    
     Entity db = new Entity(key, key);
     db.setProperty("consumerKey", creds.consumerKey);
     db.setProperty("consumerSecret", creds.consumerSecret);
     db.setProperty("accessToken", creds.accessToken);
     db.setProperty("accessTokenSecret", creds.accessTokenSecret);
     db.setProperty("apiSecret", creds.apiSecret);
     db.setProperty("appID", creds.appID);
     db.setProperty("pageID", creds.pageID);
     db.setProperty("blogID", creds.blogID);
     db.setProperty("clientID", creds.clientID);
     db.setProperty("clientSecret", creds.clientSecret);
     db.setProperty("refreshToken", creds.refreshToken);
  
     DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
     datastore.put(db);
     log.info("Ending Datastore");
     
   }
   
   /*
    * Clean Vars
    */
   private void clean()
   {
      creds.consumerKey="";
      creds.consumerSecret ="";
      creds.accessToken ="";
      creds.accessTokenSecret ="";
      creds.apiSecret ="";
      creds.appID ="";
      creds.pageID ="";
      creds.blogID ="";
      creds.clientID ="";
      creds.clientSecret = "";
      creds.refreshToken = "";
      

   }
}