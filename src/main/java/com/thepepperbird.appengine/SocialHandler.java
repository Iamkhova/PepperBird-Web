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

import com.googlecode.objectify.Result;

import static com.googlecode.objectify.ObjectifyService.ofy;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;


import twitter4j.TwitterException;
import java.util.Calendar;
import java.util.Date;


public class SocialHandler {
    public static final Logger log = Logger.getLogger(SocialHandler.class.getName());

    String                     consumerKey;
    String                     consumerSecret;
    String                     accessToken;
    String                     accessTokenSecret;
    String                     apiSecret;
    String                     appID;
    String                     pageID;
    String                     blogID;

    /*
     * Parser RSS To Datastore DB
     * public void rssParse2DB(String _rssFeed)
     */
    public void rssParse2DB(String _rssFeed) throws IOException, JSONException {
        //rss2DBGoogle(_rssFeed);
        rssParse(_rssFeed);
    }

    public void syncDB2Blogger(String _blogID) throws IOException, GeneralSecurityException {
        db2Blogger(_blogID);
    }

    public void syncSocialDB(String _label, String _url) throws IOException, GeneralSecurityException {
        blog2DB(_label, _url);
    }

    public void post2Twitter() throws IOException, TwitterException {
        blog2Twitter();
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Private Methods
    //////////////////////////////////////////////////////////////////////////////////////////////////////////

    /*
     * Store Credentials to Datastore
     * One time function to store API credentials into database
     */
    private void storeCreds2DS(String _socialType) {
        String key = _socialType; // set unique key

        log.info("Starting Datastore");

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
    private void clean() {
        consumerKey = "";
        consumerSecret = "";
        accessToken = "";
        accessTokenSecret = "";
        apiSecret = "";
        appID = "";
        pageID = "";
        blogID = "";
    }

    /*
     * Reads blogger and stores into database to be uploaded to social sties
     */
    private void blog2DB(String _label, String _url) throws IOException, GeneralSecurityException {
        int theCount;
        log.info("Starting blog2DB");
        BlogHandler blog = new BlogHandler();
        PostList post = blog.getBlogPost(_label, _url);
        String labelValue, blogTitle, blogURL, blogLabels;
        String timeStamp;

        //Text blogContent;

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        for (Post p : post.getItems()) {
            log.info("starting loop");
            labelValue = "";
            blogURL = p.getUrl().toString();
            blogTitle = p.getTitle().toString();
            Text blogContent = new Text(p.getContent());
            blogLabels = "";
            Calendar calendar = Calendar.getInstance();
            int version = 2;


            if (socialLinkNew(blogURL)) {
                //Update new social content
                timeStamp = calendar.getTime().toString();

                Entity db = new Entity("socialContent", blogURL);
                db.setProperty("syncedFacebook", "0");
                db.setProperty("syncedTwitter", "0");
                db.setProperty("syncedGoogle", "0");
                db.setProperty("title", blogTitle);
                db.setProperty("link", blogURL);
                db.setProperty("description", blogContent);
                db.setProperty("labels", blogLabels);
                db.setProperty("timeStamp", timeStamp);
                db.setProperty("version", version);

                datastore.put(db);

                log.info("Social Feed Updated/Added. Timestamp:" + timeStamp);

            }//end IF

        }//end for loop

        log.info("finished blog2db");
    }

    private void blog2Twitter() throws IOException, TwitterException {
        TwitterHandler twitter = new TwitterHandler();
        String shortTitle;
        long titleLength;

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Filter unSyncFilter = new FilterPredicate("syncedTwitter", FilterOperator.EQUAL, "0");
        Filter versionCheck = new FilterPredicate("version", FilterOperator.EQUAL, "2");

        // Use class Query to assemble a query
        Query q = new Query("socialContent").setFilter(unSyncFilter).setFilter(versionCheck);
        PreparedQuery pq = datastore.prepare(q);

        log.info("Query Starting");


        for (Entity result : pq.asIterable()) {
            String syncFacebook = (String)result.getProperty("syncedFacebook");
            log.info("Facebook Sync Handled.");

            String syncTwitter = (String)result.getProperty("syncedTwitter");
            log.info("twitter Sync Handled.");

            String syncGoogle = (String)result.getProperty("syncedGoogle");
            log.info("google Sync Handled.");

            String blogTitle = (String)result.getProperty("title");
            log.info("title Sync Handled.");

            String blogLink = (String)result.getProperty("link");
            log.info("link Sync Handled.");

            // String blogDescription = (Text) result.getProperty("description");
            String blogDescription = "";
            log.info("description Sync Handled.");

            String blogLabels = (String)result.getProperty("labels");
            log.info("label Sync Handled.");

            String version = (String)result.getProperty("version");
            log.info("label Sync Handled.");


            //Shorten Long Titles
            log.info("Title Length:" + blogTitle.length());
            titleLength = blogTitle.length();
            if (titleLength >= 80) {
                blogTitle = blogTitle.substring(0, Math.min(blogTitle.length(), 70));
                blogTitle = blogTitle + "...";
            }


            // Post to Twitter
            //TEST twitter.post2Twitter(blogTitle,blogLink,"#LIBERIA #tpbird");
            log.info("post2twitter eneaged." + blogTitle + blogLink);

            //Update synced2blog flag
            Entity db = new Entity("socialContent", blogLink);
            db.setProperty("syncedTwitter", "1");
            db.setProperty("syncedFacebook", syncFacebook);
            db.setProperty("syncedGoogle", syncGoogle);
            db.setProperty("title", blogTitle);
            db.setProperty("link", blogLink);
            db.setProperty("description", blogDescription);
            db.setProperty("labels", blogLabels);
            db.setProperty("version", version);

            datastore.put(db);

            log.info("Twitter Flag Changed to SYNC");


        }// for Loop

    }

    /*
     * Reads for Google DB and post to Blogger
     */
    private void db2Blogger(String _blogID)  {
      
      // Need to interate through article based on date and unsync status
      //       
        //  Article article = ofy().load().type(Article.class).filter("syncBlogger !=", true);
       OfyService.ofy();
       List<Article> article = ofy().load().type(Article.class).filter("syncBlogger !=", "true").list();
    

    }


    /**
     * runs the parsing of the RSS feed 
     * articles that are then stored in the Datatstore datablase
     *
     * @param  rssURL  the link to the RSS Feed to parse
     * @see         rssParse
     */
    private void rssParse(String rssURL) throws IOException, JSONException {
        RSSFeedHandler feed = new RSSFeedHandler(rssURL);
        feed.batchProcess();
    }



    /*
     * socialLinkNew Checks for duplicate links for blog post set to be socialized
     */
    private boolean socialLinkNew(String _blogUrl) {

        log.info("Starting social link check." + _blogUrl.toString());
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Filter unSyncFilter = new FilterPredicate("link", FilterOperator.EQUAL, _blogUrl);
        Filter versionCheck = new FilterPredicate("version", FilterOperator.EQUAL, "2");
        boolean pass = false;

        // Use class Query to assemble a query
        Query q = new Query("socialContent").setFilter(unSyncFilter).setFilter(versionCheck);
        log.info("Query Prepared.");
        //PreparedQuery pq = datastore.prepare(q);
        Entity entityStat = datastore.prepare(q).asSingleEntity();
        if (entityStat == null) {
            log.info("No Duplicate Entry Found");
            pass = true;
        } else {
            log.info("Duplicate Found:" + _blogUrl.toString());
            pass = false;
        }//end if

        log.info("Social Link Checking Finished.");
        return pass;

    }

    private String getCharacterDataFromElement(Element e) {
        try {
            Node child = e.getFirstChild();
            if (child instanceof CharacterData) {
                CharacterData cd = (CharacterData)child;
                return cd.getData();
            }
            // end if
        } catch (Exception ex) {
        }

        return "";
    } //private String getCharacterDataFromElement 

    private float getFloat(String value) {
        if (value != null && !value.equals(""))
            return Float.parseFloat(value);
        else
            return 0;
    }

    private String getElementValue(Element parent, String label) {
        return getCharacterDataFromElement((Element)parent.getElementsByTagName(label).item(0));
    }


}
