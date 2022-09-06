package org.apache.coyote.http11.response;

import static nextstep.jwp.utils.FileReader.readFile;

import java.io.IOException;
import org.apache.coyote.http11.request.ContentType;

public class HttpResponse {

    private final HttpStatus httpStatus;
    private final ResponseHeader header;
    private final String body;

    public HttpResponse(HttpStatus httpStatus, ResponseHeader header, String body) {
        this.httpStatus = httpStatus;
        this.header = header;
        this.body = body;
    }

    public static HttpResponse ok(String url) throws IOException {
        String requestBody = readFile(url);
        return ok(ContentType.findByUrl(url), requestBody);
    }

    public static HttpResponse ok(ContentType contentType, String requestBody) {
        return new HttpResponse(
                HttpStatus.OK,
                new ResponseHeader(contentType, requestBody.getBytes().length),
                requestBody
        );
    }

    public static HttpResponse found(String url) throws IOException {
        String responseBody = readFile(url);
        return new HttpResponse(
                HttpStatus.FOUND,
                new ResponseHeader(ContentType.HTML, responseBody.getBytes().length),
                responseBody
        );
    }

    public static HttpResponse unAuthorized() throws IOException {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        String responseBody = readFile(status.getFilePath());
        return new HttpResponse(status,
                new ResponseHeader(ContentType.findByUrl(status.getFilePath()), responseBody.getBytes().length),
                responseBody
        );
    }

    public static HttpResponse notFound() throws IOException {
        HttpStatus status = HttpStatus.NOT_FOUND;
        String responseBody = readFile(status.getFilePath());
        return new HttpResponse(status,
                new ResponseHeader(ContentType.findByUrl(status.getFilePath()), responseBody.getBytes().length),
                responseBody
        );
    }

    public String getHttpResponse() {
        return String.join("\r\n",
                "HTTP/1.1 " + httpStatus.getCode() + " " + httpStatus.getMessage() + " ",
                "Content-Type: " + header.getContentType() + ";charset=utf-8 ",
                "Content-Length: " + header.getContentLength() + " ",
                "", body);
    }
}
