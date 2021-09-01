package nextstep.jwp.http.response;

import java.util.List;
import nextstep.jwp.http.common.Body;
import nextstep.jwp.http.common.HttpVersion;
import nextstep.jwp.http.request.requestline.RequestURI;

public class HttpResponse {

    private static final String RESOURCE_ROUTE_DELIMITER = "/";
    private final StatusLine statusLine;
    private final ResponseHeaders responseHeaders;
    private final Body body;

    public HttpResponse(StatusLine statusLine,
        ResponseHeaders responseHeaders, Body body) {
        this.statusLine = statusLine;
        this.responseHeaders = responseHeaders;
        this.body = body;
    }

    public static HttpResponse of(HttpStatus httpStatus, Body body) {
        final ResponseHeaders headers = new ResponseHeaders();
        putContentTypeAndLength(body, headers, ContentType.HTML.getValue());
        return of(httpStatus, headers, body);
    }

    private static HttpResponse of(HttpStatus httpStatus, ResponseHeaders headers, Body body) {
        StatusLine statusLine = new StatusLine(HttpVersion.HTTP_1_1, httpStatus);
        return new HttpResponse(statusLine, headers, body);
    }

    public static HttpResponse of(HttpStatus httpStatus, RequestURI uri) {
        final ResponseHeaders headers = new ResponseHeaders();
        String path = setPrefix(uri.getRequestURI());
        Body body = Body.parse(path);
        putContentTypeAndLength(body, headers, ContentType.of(uri));
        return of(httpStatus, headers, body);
    }

    private static void putContentTypeAndLength(Body body, ResponseHeaders headers, String value) {
        headers.putHeader("Content-Type", value);
        headers.putHeader("Content-Length", String.valueOf(body.length()));
    }

    private static String setPrefix(String uri) {
        if (uri.startsWith(RESOURCE_ROUTE_DELIMITER)) {
            return "static" + uri;
        }
        return "static/" + uri;
    }

    public static HttpResponse redirect(String uri) {
        final ResponseHeaders headers = new ResponseHeaders();
        headers.putHeader("Location", uri);
        return of(HttpStatus.FOUND, headers, Body.empty());
    }

    public String asString() {
        String header = String
            .join(" \r\n", List.of(statusLine.asString(), responseHeaders.asString()));
        return header + "\r\n\r\n" + body.asString();
    }
}
