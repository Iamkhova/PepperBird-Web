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

public class UtilityHandler {
  
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
}
