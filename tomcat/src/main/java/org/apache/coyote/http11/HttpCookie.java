package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class HttpCookie {
    private final Map<String, String> cookies;

    private HttpCookie(final Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookie parseCookie(String cookie) {
        Map<String,String> parsedCookie = new HashMap<>();
        if(cookie!=null) {
            parsedCookie = Arrays.stream(cookie.split(";= "))
                    .takeWhile(it -> !it.isEmpty())
                    .map(it -> it.split("="))
                    .collect(Collectors.toMap(it -> it[0], it -> it[1]));
        }

        return new HttpCookie(parsedCookie);
    }

    public String makeCookieValue(UUID uuid){

        return "JSESSIONID" + "=" + uuid;
    }

    public boolean checkIdInCookie(){
        return cookies.containsKey("JSESSIONID");
    }

    public String getValue(String key){
        return cookies.get(key);
    }
}
