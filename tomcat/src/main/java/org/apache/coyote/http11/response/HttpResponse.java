package org.apache.coyote.http11.response;

import java.util.Optional;
import java.util.StringJoiner;
import org.apache.coyote.http11.ResourceProvider;

public class HttpResponse {

    private static final ResourceProvider resourceProvider = new ResourceProvider();
    private final StatusLine statusLine;
    private final HttpResponseHeaders headers;
    private final String body;

    private HttpResponse(StatusLine statusLine, HttpResponseHeaders headers, String body) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpResponse from(ResponseEntity<Object> responseEntity) {
        Optional<String> extractBody = extractBody(responseEntity);
        return new HttpResponse(
            makeStatusLine(responseEntity),
            HttpResponseHeaders.from(responseEntity, extractBody),
            extractBody.isPresent() ? extractBody.get() : null
        );
    }


    private static Optional<String> extractBody(ResponseEntity<Object> responseEntity) {
        if (haveBody(responseEntity)) {
            return Optional.of(bodyOf(responseEntity));
        }
        return Optional.empty();
    }

    private static boolean haveBody(ResponseEntity<Object> responseEntity) {
        if (responseEntity.isViewResponse()) {
            return true;
        }
        if (responseEntity.getBody() != null) {
            return true;
        }
        return false;
    }

    private static String bodyOf(ResponseEntity<Object> responseEntity) {
        if (responseEntity.isViewResponse()) {
            return resourceProvider.resourceBodyOf(responseEntity.getViewPath());
        }
        if (responseEntity.getBody() != null) {
            throw new UnsupportedOperationException("ResponseBody 에 Body 가 들어있는 경우는 아직 만들지 않았습니다.");
        }
        throw new IllegalArgumentException("body 가 존재하지 않는 응답입니다.");
    }

    private static StatusLine makeStatusLine(ResponseEntity<Object> responseEntity) {
        return new StatusLine(HttpStatusCode.of(responseEntity.getStatusCode()));
    }

    public StatusLine getStatusLine() {
        return statusLine;
    }

    public HttpResponseHeaders getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        StringJoiner responseJoiner = new StringJoiner(System.lineSeparator());
        responseJoiner.add(statusLine.toString());
        responseJoiner.add(headers.toString());
        responseJoiner.add(System.lineSeparator());
        addBody(responseJoiner);
        return responseJoiner.toString();
    }

    private void addBody(StringJoiner responseJoiner) {
        if (body != null) {
            responseJoiner.add(body);
        }
    }
}
