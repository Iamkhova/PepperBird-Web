package com.thepepperbird.appengine;


import com.googlecode.objectify.Result;

import static com.googlecode.objectify.ObjectifyService.ofy;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

import java.util.logging.Logger;
//import java.util.Date;
import java.io.IOException;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.joda.time.*;
//import java.security.GeneralSecurityException;


public class RSSFeedHandler {

    public static final Logger log = Logger.getLogger(RSSFeedHandler.class.getName());
    private String             feedUrl;
    private JSONHandler        js;
    private JSONObject         json;
    private JSONArray          entries;
    private JSONObject         objects;
    private LocalDate               date;


    private RSSFeedHandler() {
    }

    /**
     * Handles the checking and parsing of RSS feeds into 
     * articles that are then stored in the Datatstore datablase
     *
     * @param  feedURL  the link to the RSS Feed to parse
     * @see         RSSFeedHandler
     */
    public RSSFeedHandler(String feedUrl) throws IOException, JSONException {
        OfyService.ofy();
        this.feedUrl = feedUrl;
        this.js = new JSONHandler();
        this.json = (JSONObject)js.readJsonFromUrl(this.feedUrl);
        this.date = new LocalDate();


  parseJSON();
      
        log.info("Feed Parsed. Entries Found:" + this.entries.length());
    }

    /**
     * processes all articles in RSS feed in one complete batch 
     * @see         batchProcess
     */
    public void batchProcess() {
        for (int i = 0; i < this.entries.length(); i++) {
            loadEntry(i);
        }
    }


    /////
    //
    // Private Methods
    //
    ////

    private void loadEntry(int number) {

      JSoupHandler parser = new JSoupHandler();
      UtilityHandler utility = new UtilityHandler();
        String rssContent = "";
      
        this.objects = this.entries.getJSONObject(number);
        String rssTitle = new String(this.objects.getString("title"));
        String rssLink = new String(this.objects.getString("link"));
        String rssDescription = new String(this.objects.getString("contentSnippet"));
        
        rssLink = utility.cleanRSSLink(rssLink);
         
        if (!linkFound(rssLink)) {
            log.info("Virgin Query Found!..processing");
          
            // Handel getting content
    
          try{
 					rssContent = parser.getContent(rssLink);
				}catch(IOException e){}
        //    } catch (Exception ex) {log.info("Post to blogger triggered exception" + ex);
       //     }
      
            if (rssContent == "") {rssContent = rssDescription;}

            
            log.info("Cleaned Link" + rssLink);
            Article newLink = new Article(rssLink, rssTitle, rssDescription, rssContent, this.date);
            log.info("Preparing to load: " + rssTitle + " " + rssLink + " " + rssDescription);

            ofy().save().entity(newLink).now(); // async without the now()


        }


    }

    private void parseJSON() {
        this.json = (JSONObject)this.json.get("responseData");
        this.json = (JSONObject)this.json.get("feed");
        this.entries = this.json.getJSONArray("entries");
    }

    private boolean linkFound(String url) {
        boolean found = false;

        Article article = ofy().load().type(Article.class).filter("link", url).first().now();
        if (article != null) {
            found = true;
        }

        String state = String.valueOf(found);
        log.info("Link " + url + " checked for duplicates. State: " + state);

        return found;


    }


}
