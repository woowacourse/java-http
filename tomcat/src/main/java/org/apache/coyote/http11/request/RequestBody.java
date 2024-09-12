package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import org.apache.coyote.http11.helper.QueryParser;

public class RequestBody {

    private final QueryParser queryParser;
    private final String value;

    public RequestBody(BufferedReader reader, int contentLength) throws IOException {
        char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);
        this.value = new String(buffer);
        this.queryParser = QueryParser.getInstance();
    }

    public Map<String, String> parseQuery() {
        return queryParser.parse(this.value);
    }
}
