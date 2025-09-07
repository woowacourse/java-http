package org.apache.catalina.request;

import org.apache.catalina.cookie.HttpCookie;
import org.apache.coyote.HttpHeader;
import org.apache.coyote.HttpRequest;

public class ServletRequest {

    private static final char PATH_DELIMITER = '?';

    private final String method;
    private final String path;
    private final Parameters parameters;
    private final HttpHeader headers;
    private final HttpCookie cookies;
    private final String body;

    public ServletRequest(HttpRequest request) {
        this.method = request.getMethod();
        this.headers = new HttpHeader(request.getHeaders());
        this.path = parsePath(request.getUri());
        this.body = request.getBody();
        this.parameters = new Parameters(request.getUri(), body, headers.getContentType());
        this.cookies = new HttpCookie(headers.getCookie());
    }

    public String getPath() {
        return path;
    }

    public String getMethod() {
        return method;
    }

    public String getParameter(String name) {
        return parameters.getParameter(name);
    }

    public void setCookie(String name, String value){
        cookies.setCookie(name, value);
    }

    private String parsePath(String uri) {
        int queryIndex = uri.indexOf(PATH_DELIMITER);

        if (queryIndex == -1) {
            return uri;
        }
        return uri.substring(0, queryIndex);
    }
}
