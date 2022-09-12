package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import org.apache.catalina.exception.RequestBodyValueNotExists;
import org.apache.coyote.KeyValueTupleParser;

public class RequestBody {
    private final Map<String, String> content;

    private RequestBody(final String content) {
        this.content = KeyValueTupleParser.parse(content);
    }

    public static RequestBody from(final BufferedReader bufferedReader, final int contentLength) throws IOException {
        final char[] body = new char[contentLength];
        bufferedReader.read(body);
        return new RequestBody(String.valueOf(body));
    }

    public String get(final String key) {
        final String value = content.get(key);
        if (value == null) {
            throw new RequestBodyValueNotExists();
        }
        return value;
    }
}
