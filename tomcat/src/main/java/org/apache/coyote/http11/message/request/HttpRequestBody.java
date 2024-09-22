package org.apache.coyote.http11.message.request;

import java.util.Map;
import org.apache.coyote.http11.message.common.ContentType;
import org.apache.util.parser.BodyParserFactory;
import org.apache.util.parser.Parser;

public class HttpRequestBody {

    private final Map<String, String> body;

    public HttpRequestBody(Map<String, String> body) {
        this.body = body;
    }

    public HttpRequestBody(ContentType contentType, String body) {
        this.body = parseBody(contentType, body);
    }

    private Map<String, String> parseBody(ContentType contentType, String body) {
        if (body.isBlank()) {
            return Map.of();
        }
        Parser parser = BodyParserFactory.getParser(contentType);
        return parser.parse(body);
    }

    public String getBodyParameter(String key) {
        return body.get(key);
    }

    public Map<String, String> getBody() {
        return body;
    }
}
