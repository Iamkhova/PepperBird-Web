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
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.api.datastore.PreparedQuery;
import java.util.logging.Logger; 
import java.net.URLDecoder;

import java.io.UnsupportedEncodingException; 
import java.net.URLDecoder; 
import java.util.regex.*; 

public class UtilityHandler {
  
  public static final Logger log = Logger.getLogger(UtilityHandler.class.getName());
  
  public void cleanDB()
    {
    
    	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      int pageSize = 15;
      FetchOptions fetchOptions = FetchOptions.Builder.withLimit(pageSize);
     
      Query q = new Query("socialContent");
      PreparedQuery pq = datastore.prepare(q);
    
     QueryResultList<Entity> results = pq.asQueryResultList(fetchOptions);
     for (Entity e: pq.asIterable()) {
       	datastore.delete(e.getKey());
       
     }
    
        
    
    
    }
  
    public String cleanRSSLink (String _rssLink) 
    {
      String tmpString = _rssLink;
      String tmpValue;

      log.info("cleaning" + _rssLink);
      // Check url=
      tmpValue = "url=";
      if( tmpString.toLowerCase().contains(tmpValue.toLowerCase()) == true)
         {
           tmpString = tmpString.substring(tmpString.indexOf("url=")+4);
      log.info("cleaned" + tmpValue);}
      /*
      tmpValue = "%";
      if( tmpString.toLowerCase().contains(tmpValue.toLowerCase()) == true)
         {
       try{
        tmpString = convertURItoURL(tmpString);log.info("cleaned" + tmpValue);
       }catch (UnsupportedEncodingException ex) {log.info("Failure with URL decode");} 
      }
      */
      
            tmpValue = "%3F";
      if( tmpString.toLowerCase().contains(tmpValue.toLowerCase()) == true)
         {
        tmpString = tmpString.replace(tmpValue, "?");  log.info("cleaned" + tmpValue);}
      
            tmpValue = "%3D";
      if( tmpString.toLowerCase().contains(tmpValue.toLowerCase()) == true)
         {
        tmpString = tmpString.replace(tmpValue, "=");  log.info("cleaned" + tmpValue);}
      
            tmpValue = "%26";
      if( tmpString.toLowerCase().contains(tmpValue.toLowerCase()) == true)
         {
        tmpString = tmpString.replace(tmpValue, "&");  log.info("cleaned" + tmpValue);}
      
      
      tmpValue = "&pg";
      if( tmpString.toLowerCase().contains(tmpValue.toLowerCase()) == true)
         {
        tmpString = clearAfterPhrase(tmpValue, tmpString);
        log.info("cleaned" + tmpValue); }
      
      tmpValue = "&ct";
      if( tmpString.toLowerCase().contains(tmpValue.toLowerCase()) == true)
         {
        tmpString = clearAfterPhrase(tmpValue, tmpString);log.info("cleaned" + tmpValue); }
      
      tmpValue = "%E2%80%98";
      if( tmpString.toLowerCase().contains(tmpValue.toLowerCase()) == true)
         {
        tmpString = tmpString.replace(tmpValue, "‘"); log.info("cleaned" + tmpValue);}
      
      tmpValue = "%E2%80%99";
      if( tmpString.toLowerCase().contains(tmpValue.toLowerCase()) == true)
         {
        tmpString = tmpString.replace(tmpValue, "’");  log.info("cleaned" + tmpValue);}
      
      tmpValue = "%25E2%2580%2599";
      if( tmpString.toLowerCase().contains(tmpValue.toLowerCase()) == true)
         {
        tmpString = tmpString.replace(tmpValue, "’"); log.info("cleaned" + tmpValue);}
      
        tmpValue = "%25E2%2580%2598";
      if( tmpString.toLowerCase().contains(tmpValue.toLowerCase()) == true)
         {
        tmpString = tmpString.replace(tmpValue, "‘"); log.info("cleaned" + tmpValue);}
      
              tmpValue = "%2520";
      if( tmpString.toLowerCase().contains(tmpValue.toLowerCase()) == true)
         {
        tmpString = tmpString.replace(tmpValue, " "); log.info("cleaned" + tmpValue);}
                    tmpValue = "%20";
      if( tmpString.toLowerCase().contains(tmpValue.toLowerCase()) == true)
         {
        tmpString = tmpString.replace(tmpValue, " "); log.info("cleaned" + tmpValue);}
      
      
      
         log.info("clean complete:" + tmpString);
      return tmpString;
    }
  
  public String convertURItoURL (String _url)  throws UnsupportedEncodingException
    {
    log.info("converting uri to url");
    String tempURL = "";
    try{
      tempURL = java.net.URLDecoder.decode(_url, "UTF-8");
    }catch (UnsupportedEncodingException ex) {log.info("Failure with URL decode");} 
    return tempURL;
    
  }
  
  public String clearAfterPhrase (String _phrase, String _url)
    {
      String value; 
    Pattern pattern = Pattern.compile(_phrase + ".*$", Pattern.DOTALL);
    Matcher matcher = pattern.matcher(_url);
   value = matcher.replaceFirst(""); 
    
    
    return value;
  }
  
  
  
}
