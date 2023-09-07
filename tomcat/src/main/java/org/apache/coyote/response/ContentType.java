package org.apache.coyote.response;

import java.util.HashMap;
import java.util.Map;

public enum ContentType {

  TEXT_HTML("text/html"),
  TEXT_CSS("text/css"),
  TEXT_JAVASCRIPT("text/javascript"),
  IMAGE_X_ICON("image/x-icon")

  ;

  private static final Map<String, ContentType> ENUM_MAP = new HashMap<>();

  static {
    for (final ContentType type : values()) {
      ENUM_MAP.put(type.getValue(), type);
    }
  }

  private final String value;

  ContentType(final String value) {
    this.value = value;
  }

  public static ContentType findTypeFrom(final String contentType) {
    return ENUM_MAP.get(contentType);
  }

  public String getValue() {
    return value;
  }
}
