package nextstep.jwp.http.response;

import java.nio.charset.StandardCharsets;
import nextstep.jwp.http.common.HttpStatus;
import nextstep.jwp.model.StaticResource;

public class HttpResponse {

    private static final String NEW_LINE = System.getProperty("line.separator");

    private final StatusLine statusLine;
    private final ResponseHeaders responseHeaders;
    private final ResponseBody responseBody;

    public HttpResponse(StatusLine statusLine, ResponseHeaders responseHeaders, ResponseBody responseBody) {
        this.statusLine = statusLine;
        this.responseHeaders = responseHeaders;
        this.responseBody = responseBody;
    }

    public static HttpResponse of(HttpStatus httpStatus, StaticResource staticResource) {
        StatusLine statusLine = StatusLine.from(httpStatus);
        ResponseHeaders responseHeaders = ResponseHeaders.from(staticResource);
        ResponseBody responseBody = new ResponseBody(staticResource.getContent());

        return new HttpResponse(statusLine, responseHeaders, responseBody);
    }

    public byte[] toBytes() {
        if (responseBody.isEmpty()) {
            return joinToBytes(NEW_LINE, statusLine.toString(), responseHeaders.toString());
        }

        return joinToBytes(NEW_LINE, statusLine.toString(), responseHeaders.toString(), responseBody.toString());
    }

    private byte[] joinToBytes(String... strings) {
        return String.join(NEW_LINE, strings).getBytes(StandardCharsets.UTF_8);
    }
}
