package org.apache.coyote.http11.request;

import java.util.Map;
import org.apache.coyote.KeyValueTupleParser;
import org.apache.catalina.exception.RequestBodyValueNotExists;

public class RequestBody {
    private final Map<String, String> content;

    public RequestBody(final String content) {
        this.content = KeyValueTupleParser.parse(content);
    }

    public String get(final String key) {
        final String value = content.get(key);
        if (value == null) {
            throw new RequestBodyValueNotExists();
        }
        return value;
    }
}
