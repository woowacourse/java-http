package org.apache.coyote.response;

import com.techcourse.exception.UncheckedServletException;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpHeader;
import org.apache.coyote.util.FileReader;

public class HttpResponse {

    private static final FileReader FILE_READER = FileReader.getInstance();
    private static final String NOT_FOUND_FILENAME = "404.html";
    private static final String CRLF = "\r\n";

    private final HttpStatusCode httpStatusCode;
    private final HttpHeader responseHeader;
    private final String responseBody;

    public HttpResponse(HttpStatusCode httpStatusCode, String responseBody, ContentType contentType) {
        this.httpStatusCode = httpStatusCode;
        this.responseHeader = buildInitialHeaders(responseBody, contentType);
        this.responseBody = responseBody;
    }

    public static HttpResponse ofContent(String content) {
        return new HttpResponse(HttpStatusCode.OK, content, ContentType.TEXT_HTML);
    }

    public static HttpResponse ofStaticFile(String fileName, HttpStatusCode httpStatusCode) {
        if (!fileName.contains(".")) {
            fileName += ".html";
        }

        try {
            return new HttpResponse(
                    httpStatusCode,
                    FILE_READER.read(fileName),
                    ContentType.fromFileName(fileName)
            );
        } catch (UncheckedServletException e) {
            return new HttpResponse(
                    HttpStatusCode.NOT_FOUND,
                    FILE_READER.read(NOT_FOUND_FILENAME),
                    ContentType.fromFileName(fileName)
            );
        }
    }

    public static HttpResponse redirectTo(String path) {
        HttpResponse response = new HttpResponse(HttpStatusCode.FOUND, "", ContentType.TEXT_HTML);
        response.addHeader(HttpHeaderType.LOCATION.getName(), path);
        return response;
    }

    private HttpHeader buildInitialHeaders(String responseBody, ContentType contentType) {
        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeaderType.CONTENT_LENGTH.getName(), responseBody.getBytes().length + " ");
        headers.put(HttpHeaderType.CONTENT_TYPE.getName(), contentType.getName() + ";charset=utf-8 ");
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
                httpStatusCode.buildMessage(),
                responseHeader.buildMessage(),
                "",
                responseBody
        );
    }
}
