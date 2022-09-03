package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequest {

    final private String requestMethod;
    final private String requestUri;
    final private String protocol;
    final private Map<String, String> queryStrings = new HashMap<>();

    public HttpRequest(List<String> request) {
        String requestLine = request.get(0);
        String[] str = requestLine.split(" ");
        this.requestMethod = str[0];
        if (str[1].contains("?")) {
            int index = str[1].indexOf("?");
            String path = str[1].substring(0, index);
            if (!path.contains(".")) {
                this.requestUri = path + ".html";
            } else {
                this.requestUri = path;
            }
            if (!str[1].substring(index + 1).isEmpty()) {
                String queryString = str[1].substring(index + 1);
                String[] queries = queryString.split("&");
                for (String query : queries) {
                    String[] s = query.split("=");
                    queryStrings.put(s[0], s[1]);
                }
            }
        } else {
            this.requestUri = str[1];
        }
        this.protocol = str[2];
    }

    public String getRequestUri() {
        return requestUri;
    }

    public String getProtocol() {
        return protocol;
    }

    public Map<String, String> getQueryStrings() {
        return queryStrings;
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "requestMethod='" + requestMethod + '\'' +
                ", requestUri='" + requestUri + '\'' +
                ", protocol='" + protocol + '\'' +
                ", queryStrings=" + queryStrings +
                '}';
    }
}
