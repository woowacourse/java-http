package org.apache.coyote.http11.view;

import java.util.Map;
import org.apache.coyote.http11.common.HttpStatus;

public class ResponseEntity {

    private final HttpStatus httpStatus;
    private final View view;
    private final Map<String, String> headers;

    public ResponseEntity(final HttpStatus httpStatus, final View view) {
        this.httpStatus = httpStatus;
        this.view = view;
        this.headers = Map.of();
    }

    public ResponseEntity(
            final HttpStatus httpStatus,
            final View view,
            final Map<String, String> headers
    ) {
        this.httpStatus = httpStatus;
        this.view = view;
        this.headers = headers;
    }

    public static ResponseEntity fromSimpleStringData(final String body) {
        return new ResponseEntity(HttpStatus.OK, SimpleStringDataView.from(body));
    }

    public static ResponseEntity forwardTo(final String path) {
        return ResponseEntity.forwardTo(HttpStatus.OK, path);
    }

    public static ResponseEntity forwardTo(final HttpStatus httpStatus, final String path) {
        try {
            return new ResponseEntity(httpStatus, StaticResourceView.of(path));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity(HttpStatus.NOT_FOUND, StaticResourceView.of("/404.html"));
        }
    }

    public static ResponseEntity redirectTo(final String path) {
        return new ResponseEntity(
                HttpStatus.FOUND,
                StaticResourceView.of(path),
                Map.of("Location", path)
        );
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public View getView() {
        return view;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
