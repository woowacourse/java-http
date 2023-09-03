package org.apache.coyote.http11.response;

import java.util.Map;
import org.apache.coyote.http11.web.SimpleStringDataView;
import org.apache.coyote.http11.web.StaticResourceView;
import org.apache.coyote.http11.web.View;

public class ResponseEntity {

    private final HttpResponseStatus httpResponseStatus;
    private final View view;
    private final Map<String, String> headers;


    public ResponseEntity(final HttpResponseStatus httpResponseStatus, final View view) {
        this.httpResponseStatus = httpResponseStatus;
        this.view = view;
        this.headers = Map.of();
    }

    public ResponseEntity(
            final HttpResponseStatus httpResponseStatus,
            final View view,
            final Map<String, String> headers
    ) {
        this.httpResponseStatus = httpResponseStatus;
        this.view = view;
        this.headers = headers;
    }

    public static ResponseEntity of(final HttpResponseStatus httpResponseStatus, final View view) {
        return new ResponseEntity(httpResponseStatus, view);
    }

    public static ResponseEntity fromSimpleStringData(final String body) {
        return new ResponseEntity(HttpResponseStatus.OK, new SimpleStringDataView(body));
    }

    public static ResponseEntity forwardTo(final String path) {
        return ResponseEntity.forwardTo(HttpResponseStatus.OK, path);
    }

    public static ResponseEntity forwardTo(final HttpResponseStatus httpResponseStatus, final String path) {
        try {
            return new ResponseEntity(httpResponseStatus, StaticResourceView.of(path));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.of(HttpResponseStatus.NOT_FOUND, StaticResourceView.of("/404.html"));
        }
    }

    public static ResponseEntity redirectTo(final String path) {
        return new ResponseEntity(
                HttpResponseStatus.FOUND,
                StaticResourceView.of(path),
                Map.of("Location", path)
        );
    }

    public HttpResponseStatus getHttpResponseStatus() {
        return httpResponseStatus;
    }

    public View getView() {
        return view;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
