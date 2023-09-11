package org.apache.coyote.http11.header;

import java.util.Arrays;

public enum HeaderType {
  LOCATION("Location"),
  CONTENT_LENGTH("Content-Length"),
  SET_COOKIE("Set-Cookie"),
  COOKIE("Cookie"),
  CONTENT_TYPE("Content-Type"),
  ACCEPT("Accept"),
  HOST("Host"),
  CONNECTION("Connection"),
  CACHE_CONTROL("Cache-Control"),
  SEC_CH_UA("sec-ch-ua"),
  SEC_CH_UA_MOBILE("sec-ch-ua-mobile"),
  SEC_CH_UA_PLATFORM("sec-ch-ua-platform"),
  UPGRADE_INSECURE_REQUESTS("Upgrade-Insecure-Requests"),
  USER_AGENT("User-Agent"),
  SEC_FETCH_SITE("Sec-Fetch-Site"),
  SEC_FETCH_MODE("Sec-Fetch-Mode"),
  SEC_FETCH_USER("Sec-Fetch-User"),
  SEC_FETCH_DEST("Sec-Fetch-Dest"),
  ACCEPT_ENCODING("Accept-Encoding"),
  ACCEPT_LANGUAGE("Accept-Language"),
  ORIGIN("Origin"),
  REFERER("Referer"),
  PRAGMA("Pragma");

  private final String type;

  HeaderType(final String type) {
    this.type = type;
  }

  public static HeaderType from(final String type) {
    return Arrays.stream(values())
        .filter(value -> value.getType().equals(type))
        .findAny()
        .orElseThrow(() -> new IllegalArgumentException("Unknown header type: " + type));
  }

  public String getType() {
    return this.type;
  }
}
