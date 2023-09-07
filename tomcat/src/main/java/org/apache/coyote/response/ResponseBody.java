package org.apache.coyote.response;

public class ResponseBody {

  private final String value;

  public ResponseBody(final String value) {
    this.value = value;
  }

  public boolean isNotEmpty() {
    return value != null && !value.isEmpty();
  }

  public String calculateContentLength() {
    return String.valueOf(value.getBytes().length);
  }

  public String read() {
    return value;
  }
}
