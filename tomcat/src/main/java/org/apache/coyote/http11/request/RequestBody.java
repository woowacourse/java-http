package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import org.apache.coyote.http11.helper.QueryParser;

public class RequestBody {

    private static final QueryParser QUERY_PARSER = new QueryParser();

    private final String value;

    public RequestBody(BufferedReader reader, int contentLength) throws IOException {
        char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);
        this.value = new String(buffer);
    }

    public Map<String, String> parseQuery() {
        return QUERY_PARSER.parse(this.value);
    }
}
