package org.apache.coyote.http;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private static final String JSESSIONID = "JSESSIONID";
    private final Map<String, String> cookieContents;

    public HttpCookie(){
        this.cookieContents = new HashMap<>();
    }

    public HttpCookie(Map<String, String> cookieValues) {
        this.cookieContents = cookieValues;
    }

    public static HttpCookie from(final String cookies) {
        if (cookies == null || cookies.isBlank()){
            return new HttpCookie();
        }

        final HashMap<String, String> cookieMap = new HashMap<>();
        for (String cookieString : cookies.split("; ")) {
            final String[] cookie = cookieString.split("=");
            cookieMap.put(cookie[0], cookie[1]);
        }
        return new HttpCookie(cookieMap);
    }

    public boolean checkJSessionIdInCookie(){
        if(!cookieContents.containsKey(JSESSIONID)){
            return false;
        }
        return true;
    }

    public Map<String, String> ofJSessionId(final String jSessionId){
        final HashMap<String, String> setCookie = new HashMap<>();
        setCookie.put(JSESSIONID, jSessionId);
        cookieContents.put(JSESSIONID, jSessionId);
        return setCookie;
    }
}
