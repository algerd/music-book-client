
package ru.javafx.musicbook.client;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import ru.javafx.musicbook.client.properties.AuthorizationProperties;

@Component
public class SessionManager {
    
    @Autowired
    private AuthorizationProperties authorizationProperties;
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass()); 
    private String sessionId;
    private boolean expired = true;
    private long lastAccessedTime;
    
    public SessionManager() {
    }
    
    /**
     * @return sessionId 
     */
    public String getSessionId() {
        if (isExpared()) {
            authorize();
        } 
        refreshSessionTime(); 
        return sessionId;
    }
    
    public String getSessionIdCookie() {
        return "JSESSIONID=" + getSessionId();
    }
    
    private void authorize() {    
        String authorStr = authorizationProperties.getUsername() + ":" + authorizationProperties.getPassword();
        try {			                      
			URL url = new URL(authorizationProperties.getUrl());
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            
            String encoding = Base64.getEncoder().encodeToString(authorStr.getBytes("utf-8"));
			connection.setRequestProperty(HttpHeaders.AUTHORIZATION, "Basic " + encoding);                     
            String setCookie = connection.getHeaderField(HttpHeaders.SET_COOKIE);
            if (connection.getResponseCode() != 200 || setCookie == null) {
                logger.debug("Response Code {}", connection.getResponseCode());
                throw new IOException("Error Connection");               
            }           
           
            String[] arrCookies = setCookie.split(";");            
            for (String cookie : arrCookies) {
                if (cookie.startsWith("JSESSIONID")) {
                    sessionId = cookie.split("=")[1];
                    break;
                }
            }
            logger.info("JSESSIONID = {}", sessionId);
		} catch (IOException e) {
			logger.error(e.getMessage());
            //System.exit(0);
		}    
    }
   
    private void refreshSessionTime() {
        lastAccessedTime = System.currentTimeMillis();
        expired = false;
    }
   
    @Value("${server.session.timeout}")
    private int timeout;
    private boolean isExpared() {
        if (expired) {
            return true;
        }
        if (timeout > 0) {
            int timeIdle = (int) ((System.currentTimeMillis() - lastAccessedTime) / 1000L);
            if (timeIdle >= timeout) {
                expired = true;
            }
        }
        return expired;
    }
    
}
