package org.apache.http.request;

import java.util.Map;
import org.apache.http.HttpMethod;
import org.qupring.session.Session;

public interface HttpRequest {
    HttpMethod getHttpMethod();

    String getUrl();

    String getProtocol();

    Session getSession(boolean create);

    String getHeader(String target);

    Map<String, String> getQueryParams();

    Map<String, String> getBodys();

    String getBody(String target);

    Map<String, String> getCookies();

    String getCookie(String target);
}
