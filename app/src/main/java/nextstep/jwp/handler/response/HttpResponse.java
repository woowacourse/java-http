package nextstep.jwp.handler.response;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;

import nextstep.jwp.exception.handler.DefaultFileNotFoundException;
import nextstep.jwp.handler.Cookie;
import nextstep.jwp.handler.HttpBody;
import nextstep.jwp.handler.HttpCookie;
import nextstep.jwp.handler.HttpHeader;
import nextstep.jwp.handler.constant.HttpStatus;
import nextstep.jwp.util.ContentType;
import nextstep.jwp.util.File;
import nextstep.jwp.util.FileReader;

public class HttpResponse {
    public static final String LOCATION_HEADER = "Location";

    private final ResponseLine responseLine;
    private final HttpHeader header;
    private final HttpCookie cookies;
    private HttpBody body;

    public HttpResponse(String httpVersion) {
        this.responseLine = new ResponseLine(httpVersion);
        this.header = new HttpHeader(new HashMap<>());
        this.cookies = new HttpCookie();
    }

    public void ok(String url) throws IOException, URISyntaxException {
        responseLine.setHttpStatus(HttpStatus.OK);

        File file = FileReader.readFile(url);
        body(file.getContent(), file.getContentType());
    }

    public void redirect(String url) throws IOException, URISyntaxException {
        responseLine.setHttpStatus(HttpStatus.FOUND);
        addHttpHeader(LOCATION_HEADER, url);

        File file = FileReader.readFile(url);
        body(file.getContent(), file.getContentType());
    }

    public void badRequest(String url) throws DefaultFileNotFoundException {
        responseLine.setHttpStatus(HttpStatus.BAD_REQUEST);
        addHttpHeader(LOCATION_HEADER, url);

        File file = FileReader.readErrorFile(url);
        body(file.getContent(), file.getContentType());
    }

    public void unauthorized(String url) throws DefaultFileNotFoundException {
        responseLine.setHttpStatus(HttpStatus.UNAUTHORIZED);
        addHttpHeader(LOCATION_HEADER, url);

        File file = FileReader.readErrorFile(url);
        body(file.getContent(), file.getContentType());
    }

    public void notFound(String url) throws DefaultFileNotFoundException {
        responseLine.setHttpStatus(HttpStatus.NOT_FOUND);
        addHttpHeader(LOCATION_HEADER, url);

        File file = FileReader.readErrorFile(url);
        body(file.getContent(), file.getContentType());
    }

    public void internalServerError(String url) throws DefaultFileNotFoundException {
        responseLine.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        addHttpHeader(LOCATION_HEADER, url);

        File file = FileReader.readErrorFile(url);
        body(file.getContent(), file.getContentType());
    }

    public void setCookie(Cookie cookie) {
        cookies.setCookie(cookie);
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
                header.makeHttpMessage(cookies),
                "",
                body.makeHttpMessage()
        );
    }
}
