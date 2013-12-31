/*
 * Handles MD5 Conversions
 */

package com.thepepperbird.appengine;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger; 

/*
 * MD5 Handles String Conversion
 */
public class MD5
{
  public static final Logger log = Logger.getLogger(MD5.class.getName());

  public String encodeMD5(String message){
  
    String digest = null;
    try {
        
        log.info ("MD5 Class is running.");
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hash = md.digest(message.getBytes("UTF-8"));   
        StringBuilder sb = new StringBuilder(2*hash.length);
        
        for(byte b : hash){
           sb.append(String.format("%02x", b&0xff));
           digest = sb.toString();
        }// end for

        } catch (UnsupportedEncodingException ex) {
           log.warning ("UnsupportedEncodingException: " + ex + "");
        } catch (NoSuchAlgorithmException ex) {
           log.warning ("NoSuchAlgorithmException: " + ex + "");
        }

    return digest;
    
  }


}