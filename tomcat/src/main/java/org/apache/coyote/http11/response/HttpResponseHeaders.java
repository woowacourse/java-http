package org.apache.coyote.http11.response;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.http11.ResourceProvider;

public class HttpResponseHeaders {

    private static final ResourceProvider resourceProvider = new ResourceProvider();
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_LENGTH = "Content-Length";

    private final Map<String, String> headers;

    private HttpResponseHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public static HttpResponseHeaders from(ResponseEntity<Object> responseEntity, Optional<String> body) {
        Map<String, String> headers = new HashMap<>();
        headers.putAll(responseEntity.getHeaders());
        if (body.isPresent()) {
            headers.put(CONTENT_TYPE, typeOf(responseEntity));
            headers.put(CONTENT_LENGTH, body.get().getBytes().length + " ");
        }
        return new HttpResponseHeaders(headers);
    }

    private static String typeOf(ResponseEntity<Object> responseEntity) {
        if (responseEntity.isViewResponse()) {
            return resourceProvider.contentTypeOf(responseEntity.getViewPath());
        }
        if (responseEntity.getBody() != null) {
            return "application/json";
        }
        throw new IllegalArgumentException("타입을 추론할 수 없습니다.");
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
