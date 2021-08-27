package nextstep.jwp.http.response;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import nextstep.jwp.http.common.Body;
import nextstep.jwp.http.common.HttpStatus;
import nextstep.jwp.http.response.headers.ResponseHeaders;
import nextstep.jwp.http.response.statusline.StatusLine;
import nextstep.jwp.model.StaticResource;

public class HttpResponse {

    private static final String NEW_LINE = System.getProperty("line.separator");

    private final StatusLine statusLine;
    private final ResponseHeaders responseHeaders;
    private final Body body;

    public HttpResponse(StatusLine statusLine, ResponseHeaders responseHeaders, Body body) {
        this.statusLine = statusLine;
        this.responseHeaders = responseHeaders;
        this.body = body;
    }

    public static HttpResponse of(HttpStatus httpStatus, StaticResource staticResource) {
        StatusLine statusLine = StatusLine.from(httpStatus);
        ResponseHeaders responseHeaders = ResponseHeaders.from(staticResource);
        Body body = new Body(staticResource.getContent());

        return new HttpResponse(statusLine, responseHeaders, body);
    }

    public byte[] toBytes() {
        if (body.isEmpty()) {
            return joinToBytes(NEW_LINE, statusLine.toString(), responseHeaders.toString());
        }

        return joinToBytes(NEW_LINE, statusLine.toString(), responseHeaders.toString(), body.toString());
    }

    private byte[] joinToBytes(String... strings) {
        return String.join(NEW_LINE, strings).getBytes(StandardCharsets.UTF_8);
    }
}
