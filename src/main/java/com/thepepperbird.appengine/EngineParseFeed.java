/*
 * Purpose is to upload feed from DB parse it and store in DB to upload to Blogger
 */

package com.thepepperbird.appengine;


import java.net.URL; 
import javax.xml.parsers.DocumentBuilder; 
import javax.xml.parsers.DocumentBuilderFactory; 
import org.w3c.dom.CharacterData; 
import org.w3c.dom.Document; 
import org.w3c.dom.Element; 
import org.w3c.dom.Node; 
import org.w3c.dom.NodeList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import twitter4j.Twitter;
import java.util.logging.Logger;

import twitter4j.TwitterException;
import java.security.GeneralSecurityException;


public class EngineParseFeed extends HttpServlet
{
public static final Logger log = Logger.getLogger(EngineParseFeed.class.getName());


 private static final long serialVersionUID = 1L;


 public void doGet(HttpServletRequest req, HttpServletResponse resp)throws IOException
  {
      resp.setContentType("text/plain");
      BlogHandler bhh = new BlogHandler();
      SocialHandler social = new SocialHandler();
      CredentialHandler secure = new CredentialHandler();
      
      String value = req.getParameter("value");
      log.info("value" + value);
      
      if ("postTwitter".equals(value))
      {
      //TODO This stuff needs to be moved to the Credential Handler
//secure.storeBloggerCreds("31015425508-c3jcb835d4ufg9r8bl0a32ckdtr5uhou.apps.googleusercontent.com","pCdromr2AtLHBaOBuksp58ck","1/-vXNWSweiLej7iO6W4fTaEFTJALY28D1Ru3cAaLWxvw");
//secure.storeFacebookCreds("626916964013041","445226592199436","5205ac162d860a9ca6cec1b2690ce77d","CAAI6LXrxkZCEBAMZACq2IfqUeskmcAZBXxQ0TQmfns0T0JGANlTkJ46wZBdXcxzuCOTWnpfeh68mb5j5Pb5p3oG1KZALmO211mfiNUCU9AZBeG3RmFPhAC9xC2dFhmxsn8iyHyuL2nVpJeWQJ8WBxuyxoLEPF6cEpkT9OyjBusogC51iZCLt6T0IU5RXQjxAsEZD");
//secure.storeTwitterCreds("PzN7jkeEkyCY5QxQhM4A","etAYx4IlhlkKkEy6cJ2XP0P0TXzIqt1lLVpKewQ","1071710311-h2xF6A0U5mA9qG7o8Xrq9UelAkRYPciOF83Pg2A","0l2vsfLkxlafugpq4cQZuTEu5wzYtsASd9A5AnIwf0GVs");
//secure.storeBlogID(secure.LIBERIA_NEWS_BLOG, "7676001971884966148");
         try {
//              social.syncSocialDB("topnews","http://liberianews.thepepperbird.com/"); // Removed tagged twitter syncing
              social.syncSocialDB("","http://liberianews.thepepperbird.com/");
              log.info("starting posting to twitter");
              social.post2Twitter();
              log.info("end posting to twitter");
            } catch (Exception e) {
                log.info("failed" + e);
              } finally {
          
              }
      }
      
      if ("syncRSS".equals(value))
      {
          //Parse Feeds - Convert RSS to JSON and stored in Datastore
          //Only pulling true news feeds
          social.rssParse2DB("http://ajax.googleapis.com/ajax/services/feed/load?v=1.0&num=10&q=http://www.google.com/alerts/feeds/14472321683254243665/10900860890542401257");
          social.rssParse2DB("http://ajax.googleapis.com/ajax/services/feed/load?v=1.0&num=10&q=http://allafrica.com/tools/headlines/rdf/liberia/headlines.rdf");
          social.rssParse2DB("http://ajax.googleapis.com/ajax/services/feed/load?v=1.0&num=10&q=http://www.google.com/alerts/feeds/14472321683254243665/4209907976913027929");
          //social.rssParse2DB("http://ajax.googleapis.com/ajax/services/feed/load?v=1.0&num=10&q=http://www.google.com/alerts/feeds/14472321683254243665/11564968275061407926");   
          
      }//end if
      
      if ("sync2Blog".equals(value))
      {
          //Push Feeds to Blogger
          try {
               social.syncDB2Blogger(secure.getBlogID(secure.LIBERIA_NEWS_BLOG));
            } catch (Exception e) {
                log.info("failed" + e);
              } finally {
          
              }
       }//end if
  } 

}