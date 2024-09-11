package org.apache.coyote.response;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpHeader;
import org.apache.coyote.http11.HttpHeaderType;
import org.apache.coyote.util.FileReader;

public class HttpResponse {

    private static final FileReader FILE_READER = FileReader.getInstance();

    private static final String UNAUTHORIZED_FILENAME = "401.html";
    private static final String NOT_FOUND_FILENAME = "404.html";
    private static final String CRLF = "\r\n";
    private static final String FILE_DOT = ".";
    private static final String HTML_SUFFIX = ".html";
    private static final String CHARSET_UTF_8 = ";charset=utf-8";
    private static final String EMPTY_BODY = "";
    private static final String EMPTY_LINE = "";
    private static final String HTTP_1_1 = "HTTP/1.1";

    private final HttpStatusCode httpStatusCode;
    private final HttpHeader responseHeader;
    private final String responseBody;

    public HttpResponse(HttpStatusCode httpStatusCode, String responseBody, ContentType contentType) {
        this.httpStatusCode = httpStatusCode;
        this.responseHeader = buildInitialHeaders(responseBody, contentType);
        this.responseBody = responseBody;
    }

    public static HttpResponse ok(String content) {
        return new HttpResponse(HttpStatusCode.OK, content, ContentType.TEXT_HTML);
    }

    public static HttpResponse withStaticFile(String fileName) {
        if (!fileName.contains(FILE_DOT)) {
            fileName += HTML_SUFFIX;
        }

        return new HttpResponse(HttpStatusCode.OK, FILE_READER.read(fileName), ContentType.fromFileName(fileName));
    }

    public static HttpResponse unauthorized() {
        return new HttpResponse(
                HttpStatusCode.UNAUTHORIZED,
                FILE_READER.read(UNAUTHORIZED_FILENAME),
                ContentType.TEXT_HTML
        );
    }

    public static HttpResponse notFound() {
        return new HttpResponse(
                HttpStatusCode.NOT_FOUND,
                FILE_READER.read(NOT_FOUND_FILENAME),
                ContentType.TEXT_HTML
        );
    }

    public static HttpResponse redirectTo(String path) {
        HttpResponse response = new HttpResponse(HttpStatusCode.FOUND, EMPTY_BODY, ContentType.TEXT_HTML);
        response.addHeader(HttpHeaderType.LOCATION.getName(), path);
        return response;
    }

    private HttpHeader buildInitialHeaders(String responseBody, ContentType contentType) {
        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeaderType.CONTENT_LENGTH.getName(), String.valueOf(responseBody.getBytes().length));
        headers.put(HttpHeaderType.CONTENT_TYPE.getName(), contentType.getName() + CHARSET_UTF_8);
        return new HttpHeader(headers);
    }

    public void addHeader(String name, String value) {
        responseHeader.add(name, value);
    }

    public HttpResponse setCookie(HttpCookie cookie) {
        responseHeader.add(HttpHeaderType.SET_COOKIE.getName(), cookie.buildMessage());
        return this;
    }

    public byte[] getBytes() {
        return this.build().getBytes();
    }

    private String build() {
        return String.join(CRLF,
                HTTP_1_1 + " " + httpStatusCode.buildMessage(),
                responseHeader.buildMessage(),
                EMPTY_LINE,
                responseBody
        );
    }
}
