
package ru.javafx.musicbook.client.datacore;

import org.springframework.http.HttpHeaders;

public interface SessionManager {
    
    HttpHeaders createSessionHeaders();
    
    String getSessionId();
    
    String getSessionIdCookie();
    
}
