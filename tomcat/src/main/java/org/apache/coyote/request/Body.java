package org.apache.coyote.request;

import java.util.Map;

public class Body {

  private final Map<String, String> body;

  public Body(final Map<String, String> body) {
    this.body = body;
  }
}
