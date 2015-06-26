package com.thepepperbird.appengine;

import java.util.logging.Logger;
import java.util.Arrays;
import java.io.File;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.extensions.appengine.http.UrlFetchTransport;
import java.io.IOException;
import java.security.GeneralSecurityException;
import com.google.api.client.auth.oauth2.Credential;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.common.io.Files;

import com.google.api.services.blogger.Blogger;
import com.google.api.services.blogger.model.Blog;
import com.google.api.services.blogger.Blogger.Builder;
import com.google.api.services.blogger.BloggerScopes;
import com.google.api.services.blogger.Blogger.Blogs.GetByUrl;
import com.google.api.services.blogger.model.Post;
import com.google.api.services.blogger.Blogger.Posts.Insert;
import com.google.api.services.blogger.BloggerRequestInitializer;
import com.google.api.services.blogger.model.PostList;
import java.util.Collections;





public class BlogHandler
{
      public static final Logger log = Logger.getLogger(EngineParseFeed.class.getName());
      
      /*
       * Authenicate new Blogger call to Google
       * newBlogger()
       */  
      private static Blogger newBlogger() {
        
        CredentialHandler secure = new CredentialHandler();
        secure.getCredsFromDS(secure.BLOGGER_CREDS);
        //TODO Need to move these creds to database and outside of code.
        String clientID = secure.creds.clientID;
        String clientSecret = secure.creds.clientSecret;
        String refreshToken = secure.creds.refreshToken;
                 
        Credential credential;
        log.info("Verifying Google Credentials");

        credential = new GoogleCredential.Builder()
            .setTransport(new NetHttpTransport())
            .setJsonFactory(new JacksonFactory())
            .setClientSecrets(clientID, clientSecret)
            .build();
                
        log.info ("Credential Built");
                       
        credential.setRefreshToken(refreshToken);
              
        return new Blogger.Builder(new NetHttpTransport(), new JacksonFactory(), credential).setApplicationName("thePepperBird").build();
        }

    public BlogHandler() {}
    
   public PostList getBlogPost(String _keyLabel, String _url) throws IOException {
     
     log.info ("Starting getBlogListing ");
     
     Blogger blogger = newBlogger();
     Blog blog = blogger.blogs().getByUrl(_url).execute();
     
     long theValue = 20; // max results to show
     PostList post = blogger.posts().list(blog.getId().toString()).setMaxResults(theValue).execute();
     
     return post;

   }

   public void executeGetBlogByUrl (String url) throws IOException {
 
       log.info ("Starting up executeGetBlogByUrl");

       Blogger blogger = newBlogger();
       Blog blog;
       GetByUrl request = blogger.blogs().getByUrl( url );
       log.info ("DATA" + blogger.blogs().getByUrl(url));
       blog = request.execute();
       log.info ("Blog" + blog);
     }

   /*
    * Post to Blogger
    * This pulls the full content from rss link and appends it to the blogger.
    */
   public void postBlogByID (String _blogID, String _blogTitle, String _blogShortContent, String _blogRSS) throws IOException,GeneralSecurityException {
    
      String expandedContent = ""; // This holds the expanded content that we... take from the page
      JSoupHandler parser = new JSoupHandler();
      
      Blogger blogger = newBlogger();
      log.info ("Starting up postBlogByID");
       
      //Clean RSS String <--this has problems
      // _blogRSS = parser.formatRSS(_blogRSS);
             
      //Get full content
      expandedContent = parser.getContent(_blogRSS);
      if (expandedContent == "") {expandedContent = _blogShortContent;}
     
     log.info ("Expanded Content" + expandedContent);
     log.info ("Content" + _blogShortContent);
      
      //Prepare post to Blogger
      Post content = new Post();

      content.setTitle(_blogTitle);
      content.setContent(expandedContent);
     
      Insert postsInsertAction = blogger.posts().insert(_blogID, content);
     
      // Restrict the result content to just the data we need.
      postsInsertAction.setFields("author/displayName,content,published,title,url");
      
      // Disable/Enable Draft Mode
      //TODO This needs to be built as a option and not hardcoded 
      postsInsertAction.setIsDraft(true);

     // This step sends the request to the server.
     Post post = postsInsertAction.setBlogId(_blogID).execute();
     log.info ("Blog Posted.");

   }


}