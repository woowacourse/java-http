package org.apache.coyote.request;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

  private final String httpMethod;
  private final String path;
  private final Map<String, String> queryString;
  private final Body body;

  private HttpRequest(
      final String httpMethod,
      final String path,
      final Map<String, String> queryString,
      final Body body
  ) {
    this.httpMethod = httpMethod;
    this.path = path;
    this.queryString = queryString;
    this.body = body;
  }

  public static HttpRequest from(final String request) {
    final String[] element = request.split(" ");

    validateHttpRequest(element);

    final String httpMethod = element[0];
    final String path = element[1];
    final Map<String, String> queryString = makeQueryString(path);
    final Map<String, String> body = makeBody();

    return new HttpRequest(
        httpMethod,
        path,
        queryString,
        new Body(body)
    );
  }

  private static void validateHttpRequest(final String[] element) {
    if (element.length < 2) {
      throw new IllegalArgumentException("잘못된 HTTP 요청입니다.");
    }
  }

  private static Map<String, String> makeQueryString(final String path) {
    final Map<String, String> queryStringMap = new HashMap<>();
    final int queryStringIndex = path.indexOf("?");

    if (queryStringIndex < 0) {
      return Collections.emptyMap();
    }

    final String[] queryStrings = path.substring(queryStringIndex + 1)
        .split("&");

    for (final String queryString : queryStrings) {
      final String[] queryStringKeyValue = queryString.split("=");

      queryStringMap.put(queryStringKeyValue[0], queryStringKeyValue[1]);
    }

    return queryStringMap;
  }

  //TODO : Body 생기면 작성
  private static Map<String, String> makeBody() {
    return Collections.emptyMap();
  }

  public String getHttpMethod() {
    return httpMethod;
  }

  public String getPath() {
    return path;
  }

  public Map<String, String> getQueryString() {
    return queryString;
  }
}
