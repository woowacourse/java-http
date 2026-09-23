package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Optional;

public class HttpRequestBody {

    private final String body;
    private final QueryParams queryParams;

    private HttpRequestBody(String body, QueryParams queryParams) {
        this.body = body;
        this.queryParams = queryParams;
    }

    public static HttpRequestBody from(BufferedReader reader, HttpRequestHeader header) throws IOException {
        String body = "";
        QueryParams queryParams = QueryParams.from("");
        if (header.containsKey("Content-Length")) {
            int contentLength = Integer.parseInt(header.getValue("Content-Length"));
            char[] bodyChars = new char[contentLength];
            int read = reader.read(bodyChars, 0, contentLength);
            body = new String(bodyChars, 0, read);
            queryParams = QueryParams.from(body);
        }
        return new HttpRequestBody(body, queryParams);
    }

    public String getBody() {
        return body;
    }

    public Optional<String> getParameter(String key) {
        return queryParams.getValue(key);
    }
}
