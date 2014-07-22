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

import com.google.api.services.blogger.model.Post;
import com.google.api.services.blogger.model.PostList;

import twitter4j.TwitterException;






public class SocialHandler
{
   public static final Logger log = Logger.getLogger(SocialHandler.class.getName());
   
   String consumerKey;
   String consumerSecret;
   String accessToken;
   String accessTokenSecret;
   String apiSecret;
   String appID;
   String pageID;
   String blogID;
   
   /*
    * Parser RSS To Datastore DB
    * public void rssParse2DB(String _rssFeed)
    */
   public void rssParse2DB(String _rssFeed) throws IOException, JSONException 
   {
     rss2DBGoogle(_rssFeed);
   }
   
   public void syncDB2Blogger(String _blogID) throws IOException,GeneralSecurityException
   {
     db2Blogger(_blogID);
   }
   
   public void syncSocialDB(String _label, String _url)throws IOException,GeneralSecurityException
   {
     blog2DB(_label, _url);
   }
   
    public void post2Twitter()throws IOException, TwitterException
    {
      blog2Twitter();
    }
   
 
   ///////////////////////////////////////////////////////////////////////////////////////////////////////////
   // Private Methods
   //////////////////////////////////////////////////////////////////////////////////////////////////////////
   
   /*
    * Store Credentials to Datastore
    * One time function to store API credentials into database
    */
   private void storeCreds2DS(String _socialType)
   {
     String key= _socialType; // set unique key
     
     log.info ("Starting Datastore");
    
     Entity db = new Entity(key, key);
     db.setProperty("consumerKey", consumerKey);
     db.setProperty("consumerSecret", consumerSecret);
     db.setProperty("accessToken", accessToken);
     db.setProperty("accessTokenSecret", accessTokenSecret);
     db.setProperty("apiSecret", apiSecret);
     db.setProperty("appID", appID);
     db.setProperty("pageID", pageID);
     db.setProperty("blogID", blogID);
     
     
     DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
     datastore.put(db);
     log.info("Ending Datastore");
     
   }
   
   /*
    * Clean Vars
    */
   private void clean()
   {
      consumerKey="";
      consumerSecret ="";
      accessToken ="";
      accessTokenSecret ="";
      apiSecret ="";
      appID ="";
      pageID ="";
      blogID ="";
   }
   
   
   private void blog2DB(String _label, String _url) throws IOException,GeneralSecurityException
   {
     int theCount;
     log.info("Starting blog2DB");
     BlogHandler blog = new BlogHandler();
     PostList post = blog.getBlogPost(_label, _url);
     String labelValue, blogTitle, blogURL, blogLabels; 
     //Text blogContent;
     
     boolean triggerPost; // triggers match aginst key word
     
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
 
    // log.info("the count " + post.getItems());
     for (Post p : post.getItems()) {
     log.info("starting loop");
    // log.info("is empty?" + post.getItems());
       // labelValue = p.getLabels().toString();
       labelValue ="";
        triggerPost = labelValue.contains(_label);
        
        if (triggerPost){
         log.info("startting if");
          blogURL = p.getUrl().toString();
          blogTitle = p.getTitle().toString();
          Text blogContent = new Text(p.getContent());
          blogLabels ="";
        //  blogLabels = p.getLabels().toString();
         log.info("finish var store");
          //Update new social content
          Entity db = new Entity("socialContent", blogURL);
          db.setProperty("syncedFacebook", "0");
          db.setProperty("syncedTwitter", "0");
          db.setProperty("syncedGoogle", "0");
          db.setProperty("title", blogTitle);
          db.setProperty("link", blogURL);
          db.setProperty("description", blogContent);
          db.setProperty("labels", blogLabels);
          
          datastore.put(db);
 
          log.info("Social Feed Updated/Added");
        }//end IF
     }//end for loop
     log.info("finished blog2db");
   }
   
   private void blog2Twitter()throws IOException, TwitterException
   {
     TwitterHandler twitter = new TwitterHandler();
     
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
     Filter unSyncFilter = new FilterPredicate("syncedTwitter", FilterOperator.EQUAL, "0");
     
      // Use class Query to assemble a query
     Query q = new Query("socialContent").setFilter(unSyncFilter);
      PreparedQuery pq = datastore.prepare(q);

     log.info("Query Starting");


     for (Entity result : pq.asIterable()) {
       String syncFacebook = (String) result.getProperty("syncedFacebook");
       String syncTwitter = (String) result.getProperty("syncedTwitter");
       String syncGoogle = (String) result.getProperty("syncedGoogle");
       String blogTitle = (String) result.getProperty("title");
       String blogLink = (String) result.getProperty("link");
       String blogDescription = (String) result.getProperty("description");
       String blogLabels = (String) result.getProperty("labels");
   
        
       // Post to Twitter
       twitter.post2Twitter(blogTitle,blogLink,"#liberia #tpbird");
       
       //Update synced2blog flag
       Entity db = new Entity("socialContent", blogLink);
       db.setProperty("syncedTwitter", "1");
       db.setProperty("syncedFacebook", syncFacebook);
       db.setProperty("syncedGoogle", syncGoogle);
       db.setProperty("title", blogTitle);
       db.setProperty("link", blogLink);
       db.setProperty("description", blogDescription);
       db.setProperty("labels", blogLabels);

       datastore.put(db);
       
       log.info("Twitter Flag Changed to SYNC");


     }// for Loop
     
   }
   
   /*
    * Reads for Google DB and post to Blogger
    */
   private void db2Blogger(String _blogID) throws IOException,GeneralSecurityException
   {
     BlogHandler blog = new BlogHandler();
     
     log.info("Starting db2Blogger");
     // Read Datastore
     DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
     Filter unSyncFilter = new FilterPredicate("synced2blog", FilterOperator.EQUAL, "0");   
     
     // Use class Query to assemble a query
     Query q = new Query("blogContent").setFilter(unSyncFilter);
    
     // Use PreparedQuery interface to retrieve results
     PreparedQuery pq = datastore.prepare(q);
     log.info("Query Starting");

     for (Entity result : pq.asIterable()) {
       String rssTitle = (String) result.getProperty("title");
       String rssLink = (String) result.getProperty("link");
       String rssContent = "";
      // String rssContent = (String) result.getProperty("description");
       String syncState = (String) result.getProperty("synced2blog");
     
       log.info("Title:" + rssTitle);
       log.info("Link:" + rssLink);
       log.info("Description" + rssContent);
        
       // Post tp Blogger
       
       blog.postBlogByID(_blogID, rssTitle, rssContent, rssLink);
       
       //Update synced2blog flag
       Entity db = new Entity("blogContent", rssLink);
       db.setProperty("synced2blog", "1");
       db.setProperty("title", rssTitle);
       db.setProperty("link", rssLink);
       db.setProperty("description", "");

       datastore.put(db);
       
       log.info("Synced Flag Changed to SYNC");


     }// for Loop
     
   }
   
   /*
    * Handles Google JSON API Feeds and post them to Datastore
    * private void rss2DBGoogle(String _link)
    */
   private void rss2DBGoogle(String _link) throws IOException, JSONException 
   {
     JSONHandler js = new JSONHandler();
     JSONObject json = (JSONObject) js.readJsonFromUrl(_link);
     
     //Breakdown JSON to the real meat
     json = (JSONObject) json.get("responseData");
     json = (JSONObject) json.get("feed");
     JSONArray entries = json.getJSONArray("entries");
     
     String urlKey;
     log.info ("Starting RSS2DB");
     
     for(int i = 0; i < entries.length(); i++)
     {
        JSONObject objects = entries.getJSONObject(i);
        log.info ("RSSObject: " + objects.toString());
        
        String rssTitle = new String(objects.getString("title"));
        String rssLink = new String(objects.getString("link"));
        String rssContent = new String(objects.getString("content"));
        
        //Check if link already exist
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Filter rssCheck = new FilterPredicate("link",FilterOperator.EQUAL,rssLink);   
     
        // Use class Query to assemble a query
        Query q = new Query("blogContent").setFilter(rssCheck);
    
        // Use PreparedQuery interface to retrieve results
        PreparedQuery pq = datastore.prepare(q);
        
        int queryFound = pq.countEntities();
        if (queryFound == 0)
        {
            log.info("Virgin Query Found!..processing");
            
            log.info("Title:" + rssTitle);
            log.info("Link:" + rssLink);
            log.info("Description" + rssContent);
        
            // Add to Datastore
            log.info ("Starting Datastore RSS2DB");
            urlKey = rssLink;
            Entity db = new Entity("blogContent", urlKey);
            db.setProperty("title", rssTitle);
            db.setProperty("link", rssLink);
            db.setProperty("description", "");
            db.setProperty("synced2blog", "0");
        
            //DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
            datastore.put(db);
            log.info("Ending Datastore RSS2DB");
            
        }//end if

     }// end For Loop        
     
   }

    private String getCharacterDataFromElement(Element e) { 
      try 
      { 
        Node child = e.getFirstChild(); 
        if(child instanceof CharacterData) { 
            CharacterData cd = (CharacterData) child; 
            return cd.getData(); 
        } // end if
        }catch(Exception ex) { 
      } 
      
      return ""; 
    } //private String getCharacterDataFromElement 
    
    private float getFloat(String value) { 
      if(value != null && !value.equals("")) 
        return Float.parseFloat(value); 
      else 
        return 0; 
    } 
    
    private String getElementValue(Element parent,String label) { 
      return getCharacterDataFromElement((Element)parent.getElementsByTagName(label).item(0));
    } 
    

}