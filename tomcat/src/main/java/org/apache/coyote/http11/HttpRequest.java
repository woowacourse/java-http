package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequest {

    private final String startLine;
    private final Map<String, String> header;

    public HttpRequest(String startLine, Map<String, String> header) {
        this.startLine = startLine;
        this.header = header;
    }

    public static HttpRequest from(List<String> values) {
        String startLine = values.remove(0);
        values.remove(values.size()-1);
        Map<String, String> header = new HashMap<>();
        for (String value : values) {
            String[] headerValue = value.split(": ");
            header.put(headerValue[0].trim(), headerValue[1].trim());
        }
        return new HttpRequest(startLine, header);
    }

    public String uri() {
        return startLine.split(" ")[1];
    }

    public QueryString queryString() {
        String uri = uri();
        int index = uri.indexOf("?");
        return QueryString.from(uri.substring(index + 1));
    }

    public String path() {
        String uri = uri();
        int index = uri.indexOf("?");
        if (index == -1) {
            return uri;
        }
        return uri.substring(0, index);
    }
}
