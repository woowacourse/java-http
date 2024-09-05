package org.apache.coyote.http11;

import java.util.StringJoiner;

public record HttpResponse(String value) {

    private static final String DELIMITER = "\r\n";

    public static HttpResponse generate(HttpRequest request, HttpStatus httpStatus,
                                        String contentType, String responseBody) {
        StringJoiner joiner = new StringJoiner(DELIMITER);
        joiner.add("HTTP/1.1 " + httpStatus.getValue() + " ");
        addSession(request, joiner);
        joiner.add(HttpHeader.CONTENT_TYPE.getValue() + ": " + contentType + ";charset=utf-8 ");
        joiner.add(HttpHeader.CONTENT_LENGTH.getValue() + ": " + responseBody.getBytes().length + " ");
        joiner.add("");
        joiner.add(responseBody);

        return new HttpResponse(joiner.toString());
    }

    private static void addSession(HttpRequest request, StringJoiner joiner) {
        if (!request.containsSession()) {
            joiner.add(HttpHeader.SET_COOKIE.getValue() + ": " + SessionGenerator.generate());
        }
    }

    public byte[] getBytes() {
        return value.getBytes();
    }
}
