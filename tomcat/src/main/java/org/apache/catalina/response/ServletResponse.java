package org.apache.catalina.response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.catalina.cookie.HttpCookie;
import org.apache.coyote.HttpHeader;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.HttpStatus;

public class ServletResponse {

    private final HttpHeader headers;
    private final HttpCookie cookies;
    private final String protocol;
    private HttpStatus status;
    private String body;

    public ServletResponse(String protocol) {
        this.headers = new HttpHeader(new HashMap<>());
        this.cookies = new HttpCookie();
        this.protocol = protocol;
        this.body = "";
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public void sendRedirect(String location) {
        setStatus(HttpStatus.FOUND);
        headers.setLocation(location);
        this.body = "";
    }

    public void setContentType(String contentType) {
        headers.setContentType(contentType);
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void addHeader(String name, String value) {
        headers.add(name, value);
    }

    public void setCookie(String name, String value){
        cookies.setCookie(name, value);
    }

    public HttpResponse toHttpResponse() {
        HttpResponse httpResponse = new HttpResponse(protocol);

        httpResponse.setStatus(status);
        httpResponse.setBody(body);

        addCookieToHeader();
        copyHeadersTo(httpResponse);

        return httpResponse;
    }

    private void copyHeadersTo(HttpResponse httpResponse){
        Map<String, List<String>> headerMap = headers.getAllHeaders();
        for (Map.Entry<String, List<String>> entry : headerMap.entrySet()) {
            for (String headerValue : entry.getValue()) {
                httpResponse.addHeader(entry.getKey(), headerValue);
            }
        }
    }

    private void addCookieToHeader(){
        for (Entry<String, String> pair : cookies.getCookies().entrySet()) {
            addHeader("Set-Cookie", pair.getKey() + "=" + pair.getValue());
        }
    }
}
