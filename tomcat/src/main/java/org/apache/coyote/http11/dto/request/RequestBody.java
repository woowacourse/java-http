package org.apache.coyote.http11.dto.request;

import java.util.Map;
import org.apache.coyote.http11.parser.request.QueryParser;

public record RequestBody(
        String body
) {

    public Map<String, String> getQueryParam() {
        return QueryParser.parse(body);
    }
}
