package org.apache.coyote.http11.requestline;

public class RequestLine {

  private final HttpMethod method;
  private final String url;
  private final String version;
  private final HttpParam params;

  public RequestLine(final HttpMethod method, final String url, final String version,
      final HttpParam params) {
    this.method = method;
    this.url = url;
    this.version = version;
    this.params = params;
  }

  public static RequestLine from(final String requestLine) {
    final String[] tokens = requestLine.split(" ");
    final HttpMethod method = HttpMethod.from(tokens[0]);
    final String uri = tokens[1];
    final String url = parseUrl(uri);
    final HttpParam params = HttpParam.from(parseQueryString(uri));
    final String version = tokens[2];
    return new RequestLine(method, url, version, params);
  }

  private static String parseQueryString(final String uri) {
    final int index = uri.indexOf("?");
    if (index == -1) {
      return "";
    }
    return uri.substring(index + 1);
  }

  private static String parseUrl(final String uri) {
    final int index = uri.indexOf("?");
    if (index == -1) {
      return uri;
    }
    return uri.substring(0, index);
  }

  public HttpMethod getMethod() {
    return this.method;
  }

  public String getUrl() {
    return this.url;
  }

  public String getVersion() {
    return this.version;
  }

  public String getParam(final String key) {
    return this.params.get(key);
  }
}
