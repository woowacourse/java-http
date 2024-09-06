package org.apache.coyote.response;

import com.techcourse.exception.UncheckedServletException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpHeader;
import org.apache.coyote.util.FileReader;

public class HttpResponse {

    private static final FileReader FILE_READER = FileReader.getInstance();

    private static final String JSESSIONID = "JSESSIONID";
    private static final String SET_COOKIE = "Set-Cookie";
    private static final String NOT_FOUND_FILENAME = "404.html";

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
        response.addHeader(HttpHeaders.LOCATION.getName(), path);
        return response;
    }

    public HttpResponse cookie(HttpCookie cookie) {
        if (!cookie.contains(JSESSIONID)) {
            HttpCookie httpCookie = new HttpCookie();
            httpCookie.add(JSESSIONID, UUID.randomUUID().toString());
            this.addHeader(SET_COOKIE, httpCookie.buildMessage());
        }
        return this;
    }

    private HttpHeader buildInitialHeaders(String responseBody, ContentType contentType) {
        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeaders.CONTENT_LENGTH.getName(), responseBody.getBytes().length + " ");
        headers.put(HttpHeaders.CONTENT_TYPE.getName(), contentType.getName() + ";charset=utf-8 ");
        return new HttpHeader(headers);
    }

    public void addHeader(String name, String value) {
        responseHeader.add(name, value);
    }

    public String build() {
        return String.join("\r\n",
                httpStatusCode.buildMessage(),
                responseHeader.buildMessage(),
                "",
                responseBody
        );
    }
}
