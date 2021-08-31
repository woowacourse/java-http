package nextstep.jwp.response;

public class CharlieHttpResponse implements HttpResponse {

    private static final String REDIRECT_FORM = "redirect:";
    private static final String CRLF = "\r\n";
    private static final String END_OF_HEADERS = "";

    private final ResponseLine responseLine;
    private final ResponseHeader responseHeader;
    private final ResponseBody responseBody;

    public CharlieHttpResponse(ResponseLine responseLine, ResponseHeader responseHeader, ResponseBody responseBody) {
        this.responseLine = responseLine;
        this.responseHeader = responseHeader;
        this.responseBody = responseBody;
    }

    public static CharlieHttpResponse createResponse(String viewName) {
        if (viewName.startsWith(REDIRECT_FORM)) {
            String redirectLocation = viewName.substring(REDIRECT_FORM.length());
            return redirectResponse(redirectLocation);
        }
        return okResponse(viewName);
    }

    public static CharlieHttpResponse okResponse(String viewName) {
        if (viewName.startsWith("/")) {
            viewName = viewName.substring(1);
        }
        ResponseLine responseLine = ResponseLine.httpResponseLine(HttpStatusCode.OK);
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = ResponseBody.of(viewName);
        responseHeader.addHeader("Content-Type", "text/html;charset=utf-8");
        responseHeader.addHeader("Content-Length", String.valueOf(responseBody.getContentLength()));
        return new CharlieHttpResponse(responseLine, responseHeader, responseBody);
    }

    public static CharlieHttpResponse redirectResponse(String redirectLocation) {
        ResponseLine responseLine = ResponseLine.httpResponseLine(HttpStatusCode.FOUND);
        ResponseHeader responseHeader = new ResponseHeader();
        responseHeader.addHeader("Location", redirectLocation);
        return new CharlieHttpResponse(responseLine, responseHeader, ResponseBody.empty());
    }

    public String toHttpResponseMessage() {
        return String.join(CRLF,
                this.responseLine.toStatusLine(),
                this.responseHeader.toResponseHeaders(),
                END_OF_HEADERS,
                this.responseBody.toResponseMessageBody());
    }
}
