package nextstep.jwp.http.response;

import java.io.IOException;
import nextstep.jwp.http.common.HttpHeaders;
import nextstep.jwp.http.common.ResourceFile;
import nextstep.jwp.http.common.session.HttpCookie;

public class HttpResponse {

    private static final String CRLF = "\r\n";
    private static final String HTTP_VERSION = "HTTP/1.1 ";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String LOCATION = "Location";

    private HttpStatus httpStatus;
    private HttpHeaders httpHeaders;
    private String body;

    public HttpResponse() {
        this(null, HttpHeaders.of(), null);
    }

    public HttpResponse(HttpStatus httpStatus, HttpHeaders httpHeaders, String body) {
        this.httpStatus = httpStatus;
        this.httpHeaders = httpHeaders;
        this.body = body;
    }

    public void addHeader(String key, String value) {
        httpHeaders.addHeader(key, value);
    }


    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getBody() {
        return body;
    }

    public byte[] getResponseByByte() {
        return String.join(CRLF,
                getHttpLine(),
                httpHeaders.convertToLines(),
                "",
                body
        ).getBytes();
    }

    private String getHttpLine() {
        return String
                .format("%s %s %s ", HTTP_VERSION, httpStatus.value(), httpStatus.responsePhrase());
    }

    public HttpResponse forward(String uri) throws IOException {
        return forward(uri, HttpStatus.OK);
    }

    public HttpResponse forward(String uri, HttpStatus httpStatus) throws IOException {
        ResourceFile resourceFile = new ResourceFile(uri);
        String body = resourceFile.getContent();
        this.httpStatus = httpStatus;
        addHeader(CONTENT_TYPE, resourceFile.getContentType());
        addHeader(CONTENT_LENGTH, String.valueOf(body.getBytes().length));
        this.body = body;
        return this;
    }

    public HttpResponse sendRedirect(String uri) {
        return sendRedirect(uri, HttpStatus.FOUND);
    }

    public HttpResponse sendRedirect(String uri, HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
        httpHeaders.addHeader(LOCATION, uri);
        return this;
    }

    public void setCookie(HttpCookie cookie) {
        this.httpHeaders.setCookie(cookie);
    }
}
