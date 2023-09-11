package org.apache.coyote.http11.header;

public enum ContentType {
  HTML("text/html"), CSS("text/css"), JS("application/javascript");

  private final String value;

  ContentType(final String value) {
    this.value = value;
  }

  public static ContentType from(final String fileName) {
    if (fileName.endsWith(".html")) {
      return HTML;
    }
    if (fileName.endsWith(".css")) {
      return CSS;
    }
    if (fileName.endsWith(".js")) {
      return JS;
    }
    throw new IllegalArgumentException("지원하지 않는 파일 형식입니다.");
  }

  public String getValue() {
    return value;
  }
}
