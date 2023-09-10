package org.apache.coyote.http11.response;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.coyote.http11.ResourceProvider;

public class ResponseHeaders {

    private static final ResourceProvider resourceProvider = new ResourceProvider();
    private final Map<String, String> headers;

    private ResponseHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public static ResponseHeaders from(ResponseEntity<Object> responseEntity, Optional<String> body) {
        Map<String, String> headers = new HashMap<>();
        headers.putAll(responseEntity.getHeaders());
        if (body.isPresent()) {
            headers.put("Content-Type: ", typeOf(responseEntity));
            headers.put("Content-Length: ", body.get().getBytes().length + " ");
        }
        return new ResponseHeaders(headers);
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

    @Override
    public String toString() {
        return headers.keySet()
            .stream()
            .map(headerName -> makeHeader(headerName, headers.get(headerName)))
            .collect(Collectors.joining(System.lineSeparator()));
    }

    private String makeHeader(String headerName, String value) {
        return headerName + ": " + value;
    }
}
