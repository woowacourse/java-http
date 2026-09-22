package org.apache.coyote.http11;

import org.apache.coyote.http11.session.HttpCookie;

public class ResponseBuilder {

    private static final String DEFAULT_CONTENT_TYPE = "text/html";

    public String build(HttpStatus httpStatus, String accept, String responseBody) {
        String contentType = resolveContentType(accept);

        return buildWithContentType(httpStatus, contentType, responseBody);
    }

    public String buildStaticResource(HttpStatus httpStatus, String path, String responseBody) {
        return buildWithContentType(httpStatus, resolveContentTypeByPath(path), responseBody);
    }

    private String buildWithContentType(HttpStatus httpStatus, String contentType, String responseBody) {
        return String.join("\r\n",
                statusLine(httpStatus),
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    public String buildRedirect(HttpStatus httpStatus, String location) {
        return String.join("\r\n",
                statusLine(httpStatus),
                "Location: " + location + " ",
                "Content-Length: 0 ",
                "",
                "");
    }

    public String buildWithCookie(HttpStatus httpStatus, String location, HttpCookie cookie, String sessionId) {
        if (sessionId.equals(cookie.getJSessionId())) {
            return buildRedirect(httpStatus, location);
        }

        return String.join("\r\n",
                statusLine(httpStatus),
                cookie.createSetCookieHeaderForResponse(sessionId),
                "Location: " + location + " ",
                "Content-Length: 0 ",
                "",
                "");
    }

    private String statusLine(HttpStatus httpStatus) {
        return "HTTP/1.1 " + httpStatus.getCode() + " " + httpStatus.getReasonPhrase() + " ";
    }

    private String resolveContentType(String accept) {
        if (accept == null || accept.isBlank()) {
            return DEFAULT_CONTENT_TYPE;
        }

        String preferred = accept.split(",")[0]
                .split(";")[0]
                .trim();

        if (preferred.isEmpty() || preferred.equals("*/*")) {
            return DEFAULT_CONTENT_TYPE;
        }

        return preferred;
    }

    private String resolveContentTypeByPath(String path) {
        int extensionIndex = path.lastIndexOf('.');
        if (extensionIndex == -1) {
            return DEFAULT_CONTENT_TYPE;
        }

        return switch (path.substring(extensionIndex + 1).toLowerCase()) {
            case "js" -> "application/javascript";
            case "css" -> "text/css";
            case "svg" -> "image/svg+xml";
            default -> DEFAULT_CONTENT_TYPE;
        };
    }
}
