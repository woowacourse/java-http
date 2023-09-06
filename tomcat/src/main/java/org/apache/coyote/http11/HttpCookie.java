package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

  private final Map<String, String> requestCookies;

  public HttpCookie(String cookie) {
    this.requestCookies = new HashMap<>();
    if (cookie == null) {
      return;
    }
    String[] cookies = cookie.split("; ");
    for (int i = 0; i < cookies.length; i++) {
      String[] cookieTokens = cookies[i].split("=");
      requestCookies.put(cookieTokens[0], cookieTokens[1]);
    }
  }

  public boolean isExist(String key) {
    return requestCookies.get(key) != null;
  }

  public String findCookie(String key) {
    return requestCookies.get(key);
  }
}
