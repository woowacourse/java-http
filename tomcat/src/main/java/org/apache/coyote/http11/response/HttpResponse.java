package org.apache.coyote.http11.response;

import org.apache.coyote.http11.common.ContentType;
import org.apache.coyote.http11.common.Cookie;

import java.util.StringJoiner;

import static org.apache.coyote.http11.request.HttpVersion.HTTP_1_1;

public class HttpResponse {

    protected static final String RESPONSE_DELIMITER = " ";
    private static final String CRLF = "\r\n";

    private ResponseLine responseLine;
    private ResponseHeaders responseHeaders;
    private String responseBody;

    public HttpResponse() {
        this.responseLine = new ResponseLine();
        this.responseHeaders = new ResponseHeaders();
        this.responseBody = "";
    }

    public void redirect(String redirectionFile) {
        this.responseLine.redirect(HTTP_1_1);
        this.responseHeaders.addHeader("Location", redirectionFile);
        this.responseBody = "";
    }

    public void ok(String fileData, ContentType contentType) {
        this.responseHeaders.addHeader("Content-Type", contentType.getHttpContentType());
        this.responseHeaders.addHeader("Content-Length", String.valueOf(fileData.getBytes().length));
        this.responseBody = fileData;
    }

    public void addCookie(String key, String value) {
        responseHeaders.addCookie(key, value);
    }

    public String generateResponseMessage() {
        return String.join(
                CRLF,
                generateStatus(),
                generateHeader(),
                "",
                responseBody
        );
    }

    private String generateStatus() {
        return responseLine.generateMessage();
    }

    private String generateHeader() {
        StringJoiner headers = new StringJoiner("\r\n");
        headers.add(responseHeaders.generateMessage());
        headers.add(responseHeaders.generateCookieMessage());
        return headers.toString();
    }

    public Cookie getCookie() {
        return responseHeaders.getCookie();
    }
}
