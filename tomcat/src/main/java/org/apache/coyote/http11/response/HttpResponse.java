package org.apache.coyote.http11.response;

import java.util.Optional;
import org.apache.coyote.http11.ResourceProvider;

public class HttpResponse {

    private static final ResourceProvider resourceProvider = new ResourceProvider();

    private StatusLine statusLine;
    private HttpResponseHeaders headers;
    private ResponseBody body;

    public HttpResponse() {
        this(null, null, null);
    }

    private HttpResponse(StatusLine statusLine, HttpResponseHeaders headers, ResponseBody body) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.body = body;
    }

    public void responseFrom(ResponseEntity<Object> responseEntity) {
        Optional<String> extractBody = extractBody(responseEntity);
        this.statusLine = makeStatusLine(responseEntity);
        this.headers = HttpResponseHeaders.from(responseEntity, extractBody);
        this.body = extractBody.isPresent() ? new ResponseBody(extractBody.get()) : new ResponseBody(null);
    }

    private Optional<String> extractBody(ResponseEntity<Object> responseEntity) {
        if (haveBody(responseEntity)) {
            return Optional.of(bodyOf(responseEntity));
        }
        return Optional.empty();
    }

    private boolean haveBody(ResponseEntity<Object> responseEntity) {
        if (responseEntity.isViewResponse()) {
            return true;
        }
        if (responseEntity.getBody() != null) {
            return true;
        }
        return false;
    }

    private String bodyOf(ResponseEntity<Object> responseEntity) {
        if (responseEntity.isViewResponse()) {
            return resourceProvider.resourceBodyOf(responseEntity.getViewPath());
        }
        if (responseEntity.getBody() != null) {
            throw new UnsupportedOperationException("ResponseBody 에 Body 가 들어있는 경우는 아직 만들지 않았습니다.");
        }
        throw new IllegalArgumentException("body 가 존재하지 않는 응답입니다.");
    }

    private StatusLine makeStatusLine(ResponseEntity<Object> responseEntity) {
        return new StatusLine(HttpStatusCode.of(responseEntity.getStatusCode()));
    }

    public StatusLine getStatusLine() {
        return statusLine;
    }

    public HttpResponseHeaders getHeaders() {
        return headers;
    }

    public ResponseBody getBody() {
        return body;
    }
}
