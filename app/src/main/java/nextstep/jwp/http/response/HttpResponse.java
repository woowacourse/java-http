package nextstep.jwp.http.response;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpResponse {

    private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);
    private static final String STREAM_EXCEPTION = "stream exception";

    private final ResponseHeader responseHeader;
    private StatusLine statusLine;
    private ResponseBody responseBody;

    public HttpResponse() {
        this.responseHeader = new ResponseHeader();
    }

    public void forward(String uri) {
        try {
            ViewResolver viewResolver = new ViewResolver(uri);
            String content = viewResolver.getContent();
            makeResponse(content, StatusCode.OK, uri);
        } catch (IOException e) {
            log.error(STREAM_EXCEPTION);
        }
    }

    public void exception(String uri) {
        try {
            ViewResolver viewResolver = new ViewResolver(uri);
            String content = viewResolver.getContent();

            makeResponse(content, StatusCode.UNAUTHORIZED, uri);
        } catch (IOException e) {
            log.error(STREAM_EXCEPTION);
        }
    }

    private void notFound(String uri) {
        try {
            ViewResolver viewResolver = new ViewResolver(uri);
            String content = viewResolver.getContent();

            makeResponse(content, StatusCode.NOT_FOUND, uri);
        } catch (IOException e) {
            log.error(STREAM_EXCEPTION);
        }

    }

    private void makeResponse(String content, StatusCode statusCode, String uri) {
        if (Objects.isNull(content)) {
            notFound("/404.html");
            return;
        }
        setStatusLine(statusCode);
        this.responseHeader.setContentType(ContentType.findByUri(uri));
        this.responseHeader.setContentLength(content.getBytes(StandardCharsets.UTF_8).length);
        this.responseBody = new ResponseBody(content);
    }

    public void redirect(String redirectUrl) {
        setStatusLine(StatusCode.FOUND);
        this.responseHeader.setLocation(redirectUrl);
    }

    public void setStatusLine(StatusCode statusCode) {
        this.statusLine = new StatusLine(statusCode);
    }

    public byte[] getBytes() {
        return toString().getBytes();
    }

    @Override
    public String toString() {
        if (Objects.isNull(responseBody)) {
            return String.join("\r\n", statusLine.toString(), responseHeader.toString());
        }
        return String.join("\r\n", statusLine.toString(), responseHeader.toString(),
            responseBody.toString());
    }

    public void setCookie(String jsessionid) {
        this.responseHeader.setCookie(jsessionid);
    }
}
