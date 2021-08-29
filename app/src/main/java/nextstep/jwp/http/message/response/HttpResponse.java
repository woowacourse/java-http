package nextstep.jwp.http.message.response;

import static nextstep.jwp.http.Protocol.LINE_SEPARATOR;

import java.io.File;
import java.util.List;
import nextstep.jwp.http.message.element.Body;
import nextstep.jwp.http.message.element.ContentType;
import nextstep.jwp.http.message.element.Headers;
import nextstep.jwp.http.message.element.HttpStatus;
import nextstep.jwp.http.message.element.HttpVersion;
import nextstep.jwp.http.common.PathUtils;
import nextstep.jwp.http.message.response.response_line.ResponseLine;

public class HttpResponse implements Response {

    private final ResponseLine responseLine;
    private final Headers headers;
    private final Body body;

    public HttpResponse(ResponseLine responseline, Headers headers) {
        this(responseline, headers, null);
    }

    public HttpResponse(ResponseLine responseLine, Headers headers, Body body) {
        this.responseLine = responseLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpResponse status(HttpStatus httpStatus, Body body) {
        final Headers headers = new Headers();
        headers.putHeader("Content-Length", String.valueOf(body.length()));
        headers.putHeader("Content-Type", ContentType.TEXT_PLAIN.asString());

        return build(httpStatus, headers, body);
    }
    public static HttpResponse status(HttpStatus httpStatus, String path) {
        Headers headers = new Headers();

        File file = PathUtils.toFile(path);
        Body body = Body.fromFile(file);

        headers.putHeader("Content-Length", String.valueOf(body.length()));
        headers.putHeader("Content-Type", ContentType.from(path));

        return build(httpStatus, headers, body);
    }

    public static HttpResponse redirect(String location) {
        Headers headers = new Headers();
        headers.putHeader("Location", location);

        return build(HttpStatus.FOUND, headers, Body.empty());
    }

    private static HttpResponse build(HttpStatus httpStatus, Headers headers, Body body) {
        ResponseLine responseLine = new ResponseLine(HttpVersion.HTTP1_1, httpStatus);
        return new HttpResponse(responseLine, headers, body);
    }

    public String asString() {
        String topOfHeader = String.join(" " + LINE_SEPARATOR.value(), List.of(
            responseLine.asString(),
            headers.asString()
        ));

        return topOfHeader + (body == null ? "" : LINE_SEPARATOR.value().repeat(2) + body.asString());
    }
}
