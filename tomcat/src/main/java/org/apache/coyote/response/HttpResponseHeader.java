package org.apache.coyote.response;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.request.Cookie;

public class HttpResponseHeader {

  private final Map<String, Object> values = new HashMap<>();

  public HttpResponseHeader addCookie(final Cookie cookie) {
    values.put("Set-Cookie", cookie.read());
    return this;
  }

  public HttpResponseHeader addContentType(final ContentType contentType, final Charset charset) {
    values.put("Content-Type", contentType.getValue() + ";" + charset.getValue());
    return this;
  }

  public HttpResponseHeader sendRedirect(final String location) {
    values.put("Location", location);
    return this;
  }

  public String read() {
    return values.entrySet()
        .stream()
        .map(it -> it.getKey() + ": " + it.getValue())
        .collect(Collectors.joining("\r\n"));
  }
}
