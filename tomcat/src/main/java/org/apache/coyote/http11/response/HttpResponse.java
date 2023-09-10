package org.apache.coyote.http11.response;

import org.apache.coyote.http11.common.ContentType;
import org.apache.coyote.http11.common.Cookie;

import java.util.StringJoiner;

import static org.apache.coyote.http11.request.HttpVersion.HTTP_1_1;
import static org.apache.coyote.http11.response.ResponseHeaderKey.CONTENT_LENGTH;
import static org.apache.coyote.http11.response.ResponseHeaderKey.CONTENT_TYPE;
import static org.apache.coyote.http11.response.ResponseHeaderKey.LOCATION;

public class HttpResponse {

    protected static final String RESPONSE_DELIMITER = " ";
    private static final String CRLF = "\r\n";

    private ResponseLine responseLine;
    private ResponseHeaders responseHeaders;
    private ResponseBody responseBody;

    public HttpResponse() {
        this.responseLine = new ResponseLine();
        this.responseHeaders = new ResponseHeaders();
        this.responseBody = new ResponseBody();
    }

    public void redirect(String redirectionFile) {
        this.responseLine.redirect(HTTP_1_1);
        this.responseHeaders.addHeader(LOCATION.getResponseHeaderName(), redirectionFile);
    }

    public void ok(String fileData, ContentType contentType) {
        this.responseHeaders.addHeader(CONTENT_TYPE.getResponseHeaderName(), contentType.getHttpContentType());
        this.responseHeaders.addHeader(CONTENT_LENGTH.getResponseHeaderName(), String.valueOf(fileData.getBytes().length));
        this.responseBody.setResponseBody(fileData);
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
                responseBody.getResponseBody()
        );
    }

    private String generateStatus() {
        return responseLine.generateMessage();
    }

    private String generateHeader() {
        StringJoiner headers = new StringJoiner(CRLF);
        headers.add(responseHeaders.generateMessage());
        headers.add(responseHeaders.generateCookieMessage());
        return headers.toString();
    }

    public Cookie getCookie() {
        return responseHeaders.getCookie();
    }
}
