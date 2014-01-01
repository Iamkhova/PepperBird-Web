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
      
      if ("syncRSS".equals(value))
      {
          //Parse Feeds - Convert RSS to JSON and stored in Datastore
          social.rssParse2DB("http://ajax.googleapis.com/ajax/services/feed/load?v=1.0&num=10&q=http://www.google.com/alerts/feeds/14472321683254243665/10900860890542401257");
          social.rssParse2DB("http://ajax.googleapis.com/ajax/services/feed/load?v=1.0&num=10&q=http://allafrica.com/tools/headlines/rdf/liberia/headlines.rdf");
          social.rssParse2DB("http://ajax.googleapis.com/ajax/services/feed/load?v=1.0&num=10&q=http://www.google.com/alerts/feeds/14472321683254243665/7898718268626814814");
          social.rssParse2DB("http://ajax.googleapis.com/ajax/services/feed/load?v=1.0&num=10&q=http://www.google.com/alerts/feeds/14472321683254243665/11564968275061407926");   
          
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