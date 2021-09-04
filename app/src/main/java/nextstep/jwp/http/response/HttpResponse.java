package nextstep.jwp.http.response;

import java.nio.charset.StandardCharsets;
import nextstep.jwp.http.common.HttpStatus;
import nextstep.jwp.model.StaticResource;
import nextstep.jwp.server.HttpSession;

public class HttpResponse {

    private static final String NEW_LINE = System.getProperty("line.separator");
    private static final String EMPTY_STRING = "";

    private final StatusLine statusLine;
    private final ResponseHeaders responseHeaders;
    private final ResponseBody responseBody;

    public HttpResponse(StatusLine statusLine, ResponseHeaders responseHeaders,
                        ResponseBody responseBody) {
        this.statusLine = statusLine;
        this.responseHeaders = responseHeaders;
        this.responseBody = responseBody;
    }

    public static HttpResponse withBody(HttpStatus httpStatus, StaticResource staticResource) {
        StatusLine statusLine = StatusLine.from(httpStatus);
        ResponseHeaders responseHeaders = ResponseHeaders.ofBody(staticResource);
        ResponseBody responseBody = new ResponseBody(staticResource.getContent());

        return new HttpResponse(statusLine, responseHeaders, responseBody);
    }

    public static HttpResponse withBodyAndCookie(HttpStatus httpStatus, StaticResource staticResource, String cookie) {
        StatusLine statusLine = StatusLine.from(httpStatus);
        ResponseHeaders responseHeaders = ResponseHeaders.ofBodyAndSetCookie(staticResource, cookie);
        ResponseBody responseBody = new ResponseBody(staticResource.getContent());

        return new HttpResponse(statusLine, responseHeaders, responseBody);
    }

    public static HttpResponse redirect(HttpStatus httpStatus, String location) {
        StatusLine statusLine = StatusLine.from(httpStatus);
        ResponseHeaders responseHeaders = ResponseHeaders.ofRedirect(location);
        ResponseBody responseBody = ResponseBody.empty();

        return new HttpResponse(statusLine, responseHeaders, responseBody);
    }

    public byte[] toBytes() {
        return toString().getBytes(StandardCharsets.UTF_8);
    }

    public String getStatusLine() {
        return statusLine.toString();
    }

    public String getResponseHeaders() {
        return responseHeaders.toString();
    }

    @Override
    public String toString() {
        if (responseBody.isEmpty()) {
            return String.join(NEW_LINE,
                statusLine.toString(),
                responseHeaders.toString()
            );
        }

        return String.join(NEW_LINE,
            statusLine.toString(),
            responseHeaders.toString(),
            EMPTY_STRING,
            responseBody.toString()
        );
    }
}
