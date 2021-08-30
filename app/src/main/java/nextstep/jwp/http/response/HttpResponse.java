package nextstep.jwp.http.response;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import nextstep.jwp.http.common.Body;
import nextstep.jwp.http.common.HttpVersion;
import nextstep.jwp.http.request.requestline.RequestURI;

public class HttpResponse {

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
        headers.putHeader("Content-Type", ContentType.HTML.getValue());
        headers.putHeader("Content-Length", String.valueOf(body.length()));
        return of(httpStatus, headers, body);
    }

    private static HttpResponse of(HttpStatus httpStatus, ResponseHeaders headers, Body body) {
        StatusLine statusLine = new StatusLine(HttpVersion.HTTP_1_1, httpStatus);
        return new HttpResponse(statusLine, headers, body);
    }

    public static HttpResponse of(HttpStatus httpStatus, RequestURI uri) {
        final ResponseHeaders headers = new ResponseHeaders();
        String path = setPrefix(uri.getRequestURI());
        URL resource = Objects.requireNonNull(ClassLoader.getSystemResource(path));
        File file = new File(resource.getPath());
        Body body = Body.parse(file);
        headers.putHeader("Content-Type", ContentType.of(uri));
        headers.putHeader("Content-Length", String.valueOf(body.length()));
        return of(httpStatus, headers, body);


    }

    private static String setPrefix(String uri) {
        if (uri.startsWith("/")) {
            return "static" + uri;
        }
        return "static/" + uri;
    }

    public String asString() {
        String header = String
            .join(" \r\n", List.of(statusLine.asString(), responseHeaders.asString()));
        return header + "\r\n\r\n" + body.asString();
    }
}
