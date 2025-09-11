package com.spring.http.request;

import com.spring.http.HttpHeader;
import com.spring.http.enums.HttpStatus;
import com.techcourse.exception.HttpStatusException;
import java.util.Map;
import org.apache.catalina.domain.Session;
import org.apache.catalina.manager.SessionManager;

public record HttpRequest(
        RequestStartLine requestStartLine,
        Map<String, String> queryStrings,
        HttpHeader header,
        HttpRequestBody body
) {

    private static final String URL_ENCODED = "application/x-www-form-urlencoded";

    public HttpRequest(RequestStartLine requestStartLine, Map<String, String> queryStrings, HttpHeader header) {
        this(requestStartLine, queryStrings, header, null);
    }

    public Map<String, String> parseBody() {
        validateParsingBody();
        final String contentType = header.getContentType();

        if (contentType.startsWith(URL_ENCODED)) {
            return body.parseFormData();
        }

        throw new HttpStatusException("Unsupported Content-Type: " + contentType, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    private void validateParsingBody() {
        final String contentType = header.getContentType();

        if (contentType == null) {
            throw new HttpStatusException("Content-Type header is missing",
                    HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        }

        if (body == null) {
            throw new HttpStatusException("Request body is missing", HttpStatus.BAD_REQUEST);
        }
    }

    public Session getSession(boolean create) {
        SessionManager sessionManager = new SessionManager();

        return sessionManager.getSession(this, create);
    }
}
