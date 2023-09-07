package org.apache.coyote.response;

public enum Charset {

  UTF_8("utf-8")
  ;

  private final String value;

  Charset(final String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
