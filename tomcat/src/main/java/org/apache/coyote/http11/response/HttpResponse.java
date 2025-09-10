package org.apache.coyote.http11.response;

import org.apache.coyote.http11.common.ContentType;
import org.apache.coyote.http11.common.Cookies;
import org.apache.coyote.http11.common.Headers;

public class HttpResponse {

    private ContentType contentType = ContentType.NONE;
    private HttpStatus httpStatus = HttpStatus.OK;
    private Headers headers = new Headers();
    private String responseBody = "";
    private Cookies responseCookies = new Cookies();

    public ContentType getContentType() {
        return contentType;
    }

    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public Headers getHeaders() {
        return headers;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public Cookies getResponseCookies() {
        return responseCookies;
    }

    public boolean isStaticPage() {
        return contentType.isText() && !httpStatus.is3xx();
    }

    public String buildResponse() {
        int bodyLength = getBodyLength(getResponseBody());
        return "HTTP/1.1 " + getHttpStatus().getCode() + " " + getHttpStatus().getName() + "\r\n"
            + "Content-Type: " + getContentType().getType() + ";charset=utf-8" + "\r\n"
            + "Content-Length: " + bodyLength + "\r\n"
            + addIfNotEmpty(getHeaders().toString())
            + addIfNotEmpty(getResponseCookies().toString())
            + "\r\n" + getResponseBody();
    }

    private String addIfNotEmpty(String value) {
        if (value.isEmpty()) {
            return "";
        }
        return value + "\r\n";
    }

    private int getBodyLength(String responseBody) {
        if (responseBody == null) {
            return 0;
        }
        return responseBody.getBytes().length;
    }
}
