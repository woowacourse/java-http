package org.apache.coyote.http11.request.body;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
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
        return Optional.ofNullable(params.get(key))
                .orElseThrow(() -> new IllegalArgumentException(key + " 에 해당하는 파라미터를 찾을 수 없습니다."));
    }
}
