package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequest {

    private HttpMethod method;
    private String path;
    private Map<String, String> queryProperties;

    private Map<String, String> headers;

    private String body;

    public HttpRequest(final String startLine, final Map<String, String> headers,
                       final String body) {
        final List<String> startLineTokens = List.of(startLine.split(" "));
        this.method = HttpMethod.valueOf(startLineTokens.get(0));
        String uri = startLineTokens.get(1);
        int uriSeparatorIndex = uri.indexOf("?");
        if (uriSeparatorIndex == -1) {
            this.path = uri;
        } else {
            this.path = uri.substring(0, uriSeparatorIndex);
            this.queryProperties = makeQueryProperties(uri.substring(uriSeparatorIndex + 1));
        }
        this.headers = headers;
        this.body = body;

    }

    private Map<String, String> makeQueryProperties(final String queryString) {
        Map<String, String> result = new HashMap<>();
        String[] queryTokens = queryString.split("&");
        for (String queryToken : queryTokens) {
            int equalSeparatorIndex = queryToken.indexOf("=");
            if (equalSeparatorIndex != -1) {
                result.put(queryToken.substring(0, equalSeparatorIndex),
                        queryToken.substring(equalSeparatorIndex + 1));
            }
        }
        return result;
    }
 
    public HttpCookie getCookie() {
        return new HttpCookie(this.headers.get("Cookie"));
    }

    public boolean isSamePath(String path) {
        return this.path.equals(path);
    }

    public boolean isPOST() {
        return this.method.equals(HttpMethod.POST);
    }

    public boolean isGET() {
        return this.method.equals(HttpMethod.GET);
    }

    public boolean isNotExistBody() {
        return this.body == null;
    }

    public String getBody() {
        return this.body;
    }

    public String getAccept() {
        return this.headers.get("Accept");
    }

    public String getPath() {
        return this.path;
    }

}
