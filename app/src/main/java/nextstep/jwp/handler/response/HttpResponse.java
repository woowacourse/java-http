package nextstep.jwp.handler.response;

import java.util.HashMap;

import nextstep.jwp.handler.HttpBody;
import nextstep.jwp.handler.HttpHeader;
import nextstep.jwp.handler.constant.HttpStatus;
import nextstep.jwp.handler.request.HttpRequest;
import nextstep.jwp.handler.response.ResponseLine;
import nextstep.jwp.util.ContentType;
import nextstep.jwp.util.File;

public class HttpResponse {
    public static final String LOCATION_HEADER = "Location";

    private final ResponseLine responseLine;
    private final HttpHeader header;
    private HttpBody body;

    public HttpResponse(HttpRequest httpRequest) {
        this.responseLine = new ResponseLine(httpRequest.getHttpVersion());
        this.header = new HttpHeader(new HashMap<>());
    }

    public void ok(File file) {
        responseLine.setHttpStatus(HttpStatus.OK);
        body(file.getContent(), file.getContentType());
    }

    public void redirect(String url, File file) {
        responseLine.setHttpStatus(HttpStatus.FOUND);
        addHttpHeader(LOCATION_HEADER, url);
        body(file.getContent(), file.getContentType());
    }

    public void unauthorized(String url, File file) {
        responseLine.setHttpStatus(HttpStatus.UNAUTHORIZED);
        addHttpHeader(LOCATION_HEADER, url);
        body(file.getContent(), file.getContentType());
    }

    public void notFound(String url, File file) {
        responseLine.setHttpStatus(HttpStatus.NOT_FOUND);
        addHttpHeader(LOCATION_HEADER, url);
        body(file.getContent(), file.getContentType());
    }

    private void body(String body, ContentType contentType) {
        this.header.addHeader("Content-Type", contentType.getValue());
        this.header.addHeader("Content-Length", String.valueOf(body.getBytes().length));
        this.body = new HttpBody(body);
    }
    public void addHttpHeader(String key, String value) {
        this.header.addHeader(key, value);
    }

    public String makeHttpMessage() {
        return String.join(
                "\r\n",
                responseLine.makeHttpMessage(),
                header.makeHttpMessage(),
                "",
                body.makeHttpMessage()
        );
    }
}
