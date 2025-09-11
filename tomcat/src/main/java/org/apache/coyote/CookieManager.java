package org.apache.coyote;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class CookieManager {

    private Map<String, String> cookieMap;

    public CookieManager() {
        cookieMap = new HashMap<>();
    }

    public CookieManager(String cookieLine) {
        cookieMap = new HashMap<>();
        parseCookies(cookieLine);
    }

    public void addCookie(String key, String val) {
        cookieMap.put(key, val);
    }

    public void parseCookies(String cookieLine){
        String[] cookies = cookieLine.split(";");
        for(String cookie : cookies){
            cookie = cookie.trim();
            int idx = cookie.indexOf("=");
            if(idx == -1){
                addCookie(cookie, "");
                return;
            }
            addCookie(
                    cookie.substring(0, idx),
                    cookie.substring(idx+1)
            );
        }
    }

    public String generateCookieLine(){
        StringJoiner sj = new StringJoiner("; ");
        for (Map.Entry<String, String> entry : cookieMap.entrySet()) {
            sj.add(entry.getKey() + "=" + entry.getValue());
        }
        return sj.toString();
    }

    public boolean containsCookieKey(String cookieKey){
        return cookieMap.containsKey(cookieKey);
    }

    public Map<String, String> getCookieMap() {
        return cookieMap;
    }

    public String getCookie(String cookieKey){
        return cookieMap.get(cookieKey);
    }
}
