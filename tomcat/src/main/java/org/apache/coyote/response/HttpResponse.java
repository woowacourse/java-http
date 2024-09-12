package org.apache.coyote.response;

import org.apache.coyote.exception.CoyoteException;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpHeader;
import org.apache.coyote.http11.HttpHeaderType;
import org.apache.coyote.util.FileReader;

public class HttpResponse {

    private static final FileReader FILE_READER = FileReader.getInstance();

    private static final String UNAUTHORIZED_FILENAME = "401.html";
    private static final String NOT_FOUND_FILENAME = "404.html";
    private static final String INTERNAL_SERVER_ERROR_FILENAME = "500.html";
    private static final String CRLF = "\r\n";
    private static final String CHARSET_UTF_8 = ";charset=utf-8";
    private static final String EMPTY_LINE = "";
    private static final String HTTP_1_1 = "HTTP/1.1";

    private final HttpHeader responseHeader;

    private HttpStatusCode httpStatusCode;
    private String responseBody;

    public HttpResponse() {
        responseHeader = new HttpHeader();
    }

    public void setRedirectWithSessionId(String path, String sessionId) {
        setRedirect(path);
        setCookie(HttpCookie.ofSessionId(sessionId));
    }

    public void setCookie(HttpCookie cookie) {
        responseHeader.add(HttpHeaderType.SET_COOKIE.getName(), cookie.buildMessage());
    }

    public void setRedirect(String path) {
        httpStatusCode = HttpStatusCode.FOUND;
        responseHeader.add(HttpHeaderType.LOCATION.getName(), path);
    }

    public void setUnauthorized() {
        httpStatusCode = HttpStatusCode.UNAUTHORIZED;
        setContentType(ContentType.fromFileName(UNAUTHORIZED_FILENAME));
        setResponseBody(FILE_READER.read(UNAUTHORIZED_FILENAME));
    }

    public void setStaticResource(String fileName) {
        try {
            httpStatusCode = HttpStatusCode.OK;
            setContentType(ContentType.fromFileName(fileName));
            setResponseBody(FILE_READER.read(fileName));
        } catch (CoyoteException e) {
            httpStatusCode = HttpStatusCode.NOT_FOUND;
            setContentType(ContentType.fromFileName(NOT_FOUND_FILENAME));
            setResponseBody(FILE_READER.read(NOT_FOUND_FILENAME));
        }
    }

    public void setInternalServerError() {
        httpStatusCode = HttpStatusCode.INTERNAL_SERVER_ERROR;
        setContentType(ContentType.fromFileName(INTERNAL_SERVER_ERROR_FILENAME));
        setResponseBody(FILE_READER.read(INTERNAL_SERVER_ERROR_FILENAME));
    }

    private void setResponseBody(String rawBody) {
        responseBody = rawBody;
        responseHeader.add(
                HttpHeaderType.CONTENT_LENGTH.getName(),
                String.valueOf(responseBody.getBytes().length)
        );
    }

    private void setContentType(ContentType contentType) {
        responseHeader.add(HttpHeaderType.CONTENT_TYPE.getName(), contentType.getName() + CHARSET_UTF_8);
    }

    public byte[] getBytes() {
        String rawResponse = String.join(CRLF,
                HTTP_1_1 + " " + httpStatusCode.buildMessage(),
                responseHeader.buildMessage(),
                EMPTY_LINE,
                responseBody
        );

        return rawResponse.getBytes();
    }
}
