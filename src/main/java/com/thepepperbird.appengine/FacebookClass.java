package com.thepepperbird.appengine;

import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.FacebookFactory;
import facebook4j.auth.AccessToken;
import java.util.logging.Logger;

public class FacebookClass
{
public static final Logger log = Logger.getLogger(FacebookClass.class.getName());

  public void fbpost()
  {
    try {
          Facebook facebook = new FacebookFactory().getInstance();
          String appID = "626916964013041";
          String pageID = "445226592199436";
          String appSecret = "5205ac162d860a9ca6cec1b2690ce77d";
          String accessToken = "CAAI6LXrxkZCEBAMZACq2IfqUeskmcAZBXxQ0TQmfns0T0JGANlTkJ46wZBdXcxzuCOTWnpfeh68mb5j5Pb5p3oG1KZALmO211mfiNUCU9AZBeG3RmFPhAC9xC2dFhmxsn8iyHyuL2nVpJeWQJ8WBxuyxoLEPF6cEpkT9OyjBusogC51iZCLt6T0IU5RXQjxAsEZD";
  
          facebook.setOAuthAppId(appID, appSecret);
           // facebook.setOAuthPermissions(commaSeparetedPermissions);
          facebook.setOAuthAccessToken(new AccessToken(accessToken, null));
          facebook.postStatusMessage(pageID, "[CodeTest] Test post directly from ThePepperBird.com servers. :)");
          }catch (FacebookException ex) {
            log.info("Error" + ex);
          }
    
  }

}