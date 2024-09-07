package org.apache.coyote.http11.request;

import java.util.Collections;
import java.util.Map;
import org.apache.coyote.http11.request.bodyparser.RequestBodyParser;

public class RequestBody {

    private final Map<String, String> body;

    public RequestBody() {
        this(Collections.emptyMap());
    }

    public RequestBody(String body, RequestBodyParser parser) {
        this(parser.parseParameters(body));
    }

    private RequestBody(Map<String, String> body) {
        this.body = body;
    }

    public Map<String, String> getBody() {
        return body;
    }
}
