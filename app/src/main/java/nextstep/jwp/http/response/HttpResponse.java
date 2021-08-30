package nextstep.jwp.http.response;

import java.io.IOException;
import nextstep.jwp.http.common.HttpHeaders;
import nextstep.jwp.http.common.ResourceFile;

public class HttpResponse {

    private static final String NEWLINE = "\r\n";
    private static final String HTTP_VERSION = "HTTP/1.1 ";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";


    private HttpStatus httpStatus;
    private HttpHeaders httpHeaders;
    private String body = "";

    public HttpResponse() {
        this.httpHeaders = HttpHeaders.of();
    }

    public HttpResponse(HttpStatus httpStatus, HttpHeaders httpHeaders, String body) {
        this.httpStatus = httpStatus;
        this.httpHeaders = httpHeaders;
        this.body = body;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public void addHeader(String key, String value) {
        httpHeaders.addHeader(key, value);
    }

    public void setBody(String body) {
        this.body = body;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getBody() {
        return body;
    }

    public byte[] getResponseByByte() {
        return String.join(NEWLINE,
                getHttpLine(),
                httpHeaders.convertToLines(),
                "",
                body
        ).getBytes();
    }

    private String getHttpLine() {
        return HTTP_VERSION + httpStatus.value() + " " + httpStatus.responsePhrase() + " ";
    }

    public HttpResponse forward(String uri) throws IOException {
        return forward(uri, HttpStatus.OK);
    }

    public HttpResponse forward(String uri, HttpStatus httpStatus) throws IOException {
        ResourceFile resourceFile = new ResourceFile(uri);
        String body = resourceFile.getContent();
        setHttpStatus(httpStatus);
        addHeader("Content-Type", resourceFile.getContentType());
        addHeader("Content-Length", String.valueOf(body.getBytes().length));
        setBody(body);
        return this;
    }

    public HttpResponse sendRedirect(String uri) {
        return sendRedirect(uri, HttpStatus.FOUND);
    }

    public HttpResponse sendRedirect(String uri, HttpStatus httpStatus) {
        setHttpStatus(httpStatus);
        httpHeaders.addHeader("Location", uri);
        return this;
    }
}
