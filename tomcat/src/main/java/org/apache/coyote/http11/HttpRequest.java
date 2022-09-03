package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
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

    public static HttpRequest from(BufferedReader bufferedReader) throws IOException {
        String requestLine = bufferedReader.readLine();
        Map<String, String> requestHeader = new HashMap<>();
        while (bufferedReader.ready()) {
            String line = bufferedReader.readLine();
            if (line.equals("")) {
                break;
            }
            String[] headerValue = line.split(": ");
            requestHeader.put(headerValue[0], headerValue[1]);
        }
        return new HttpRequest(requestLine, requestHeader);
    }

    public String path() {
        String uri = uri();
        int index = uri.indexOf("?");
        if (index == -1) {
            return uri;
        }
        return uri.substring(0, index);
    }

    public QueryString queryString() {
        String uri = uri();
        String[] pathAndQueryString = uri.split("\\?");
        if (pathAndQueryString.length <= 1) {
            return new QueryString(new HashMap<>());
        }
        return QueryString.from(pathAndQueryString[1]);
    }

    private String uri() {
        return startLine.split(" ")[1];
    }
}
