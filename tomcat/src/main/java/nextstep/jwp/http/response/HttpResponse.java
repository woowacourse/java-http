package nextstep.jwp.http.response;

import java.util.StringJoiner;
import nextstep.jwp.http.HttpCookie;
import nextstep.jwp.http.HttpHeader;

public class HttpResponse {

    private static final String BLANK = " ";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String LOCATION = "Location";
    private static final String SET_COOKIE = "Set-Cookie";

    private String version = "HTTP/1.1";
    private StatusCode statusCode;
    private HttpHeader httpHeaders = new HttpHeader();
    private String responseBody;

    public HttpResponse() {
    }

    public HttpResponse(final StatusCode statusCode, final String contentType) {
        this.statusCode = statusCode;
        this.httpHeaders.addValue(CONTENT_TYPE, contentType);
    }

    public void sendRedirect(final String location) {
        statusCode = StatusCode.FOUND;
        httpHeaders.addValue(LOCATION, location);
    }

    public void setContentLength(final int contentLength) {
        this.httpHeaders.addValue(CONTENT_LENGTH, String.valueOf(contentLength));
    }

    public void setResponseBody(final String responseBody) {
        this.responseBody = responseBody;
    }

    public void setCookie(final HttpCookie cookie) {
        this.httpHeaders.addValue(SET_COOKIE, cookie.getCookie());
    }

    public String getResponseTemplate() {
        String statusLineTemplate = createStatusLineTemplate();
        String headerTemplate = httpHeaders.createHeaderTemplate();
        String responseBodyTemplate = createResponseBodyTemplate();

        return statusLineTemplate + headerTemplate + responseBodyTemplate;
    }

    private String createStatusLineTemplate() {
        StringJoiner statusLineJoiner = new StringJoiner(BLANK);
        statusLineJoiner.add(version);
        statusLineJoiner.add(statusCode.getCode());
        statusLineJoiner.add(statusCode.getMessage());
        statusLineJoiner.add("\r\n");
        return statusLineJoiner.toString();
    }

    private String createResponseBodyTemplate() {
        return "\r\n"
                + "\r\n"
                + responseBody;
    }
}
