package com.techcourse.web.view;

import static common.HttpConstants.LOCATION_HEADER_NAME;

import common.ContentType;
import common.HttpStatus;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public record StandardResponse(
        HttpStatus status,
        Map<String, String> headers,
        byte[] body
) implements AppResponse {

    public static StandardResponse ok(final Map<String, String> header, final String body) {
        return new StandardResponse(HttpStatus.OK, header, body.getBytes());
    }

    public static StandardResponse ok(final ContentType contentType, final String body) {
        final Map<String, String> header = new LinkedHashMap<>();
        header.put(ContentType.HEADER_NAME, contentType.getMimeTypeAndCharset());
        return ok(header, body);
    }

    public static StandardResponse found(final Map<String, String> header) {
        final HttpStatus httpStatus =
                (header.get(LOCATION_HEADER_NAME) == null) ? HttpStatus.NOT_FOUND : HttpStatus.FOUND;
        return new StandardResponse(httpStatus, header, new byte[0]);
    }

    public static StandardResponse found(final String location) {
        final Map<String, String> header = new LinkedHashMap<>();
        header.put(LOCATION_HEADER_NAME, location);
        return found(header);
    }

    public static StandardResponse notFound() {
        return new StandardResponse(HttpStatus.NOT_FOUND, new HashMap<>(), new byte[0]);
    }

    public static StandardResponse methodNotAllowed() {
        return new StandardResponse(HttpStatus.METHOD_NOT_ALLOWED, new HashMap<>(), new byte[0]);
    }

    public static StandardResponse serverError() {
        return new StandardResponse(HttpStatus.INTERNAL_SERVER_ERROR, new HashMap<>(), new byte[0]);
    }
}
