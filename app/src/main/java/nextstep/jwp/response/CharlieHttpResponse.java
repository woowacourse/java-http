package nextstep.jwp.response;

import nextstep.jwp.web.model.Cookie;

public class CharlieHttpResponse implements HttpResponse {

    private static final String CONTENT_TYPE_HEADER_NAME = "Content-Type";
    private static final String CONTENT_LENGTH_HEADER_NAME = "Content-Length";
    private static final String LOCATION_HEADER_NAME = "Location";
    private static final String SET_COOKIE_HEADER_NAME = "Set-Cookie";
    private static final String REDIRECT_FORM = "redirect:";
    private static final String CRLF = "\r\n";
    private static final String END_OF_HEADERS = "";

    private ResponseLine responseLine;
    private ResponseHeader responseHeader;
    private ResponseBody responseBody;

    public CharlieHttpResponse() {
        this(null, new ResponseHeader(), null);
    }

    public CharlieHttpResponse(ResponseLine responseLine, ResponseHeader responseHeader, ResponseBody responseBody) {
        this.responseLine = responseLine;
        this.responseHeader = responseHeader;
        this.responseBody = responseBody;
    }

    public void setView(String viewName, HttpStatusCode statusCode) {
        if (viewName.startsWith(REDIRECT_FORM)) {
            String redirectLocation = viewName.substring(REDIRECT_FORM.length()).trim();
            redirectResponse(redirectLocation);
            return;
        }
        if (viewName.startsWith("/")) {
            viewName = viewName.substring(1);
        }
        this.responseLine = ResponseLine.httpResponseLine(statusCode);
        this.responseBody = ResponseBody.of(viewName);
        this.responseHeader.addHeader(CONTENT_TYPE_HEADER_NAME, ContentType.getValue(viewName));
        this.responseHeader.addHeader(CONTENT_LENGTH_HEADER_NAME, String.valueOf(responseBody.getContentLength()));
    }

    public void redirectResponse(String redirectLocation) {
        this.responseLine = ResponseLine.httpResponseLine(HttpStatusCode.FOUND);
        this.responseHeader.addHeader(LOCATION_HEADER_NAME, redirectLocation);
        this.responseBody = ResponseBody.empty();
    }

    public void addCookie(Cookie cookie) {
        responseHeader.addHeader(SET_COOKIE_HEADER_NAME, cookie.toSetMessage());
    }

    public String toHttpResponseMessage() {
        return String.join(CRLF,
                this.responseLine.toStatusLine(),
                this.responseHeader.toResponseHeaders(),
                END_OF_HEADERS,
                this.responseBody.toResponseMessageBody());
    }
}
