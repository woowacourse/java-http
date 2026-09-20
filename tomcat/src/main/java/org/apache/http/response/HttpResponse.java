package org.apache.http.response;


import java.util.Map;

public interface HttpResponse {

    int getStatus();

    void setStatus(int statusCode);

    String getHeader(String name);

    void setHeader(String name, String value);

    Map<String, String> getHeaders();

    String getBody();

    void setBody(String body);

    void setLocation(String path);

    void setCookie(String cookie);
}
