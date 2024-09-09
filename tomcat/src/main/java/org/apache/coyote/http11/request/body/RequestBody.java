package org.apache.coyote.http11.request.body;

import java.util.Collections;
import java.util.Map;
import org.apache.coyote.http11.request.body.parser.RequestBodyParserContext;

public class RequestBody {

    private final Map<String, String> params;

    public RequestBody(Map<String, String> params) {
        this.params = params;
    }

    public RequestBody(String mediaType, String body) {
        this.params = RequestBodyParserContext.parse(mediaType, body);
    }

    public static RequestBody empty() {
        return new RequestBody(Collections.emptyMap());
    }

    public String get(String key) {
        return params.get(key);
    }
}
