package nextstep.jwp.http;

import java.util.ArrayList;
import java.util.List;
import nextstep.jwp.http.entity.HttpBody;
import nextstep.jwp.http.entity.HttpHeaders;
import nextstep.jwp.http.entity.HttpStatus;
import nextstep.jwp.http.entity.HttpVersion;

public class HttpResponse {
    private HttpVersion httpVersion;
    private HttpStatus httpStatus;
    private HttpHeaders httpHeaders;
    private HttpBody httpBody;

    public HttpResponse(HttpStatus httpStatus, HttpHeaders httpHeaders, HttpBody httpBody) {
        this(HttpVersion.HTTP_1_1, httpStatus, httpHeaders, httpBody);
    }

    public HttpResponse(HttpVersion httpVersion, HttpStatus httpStatus, HttpHeaders httpHeaders,
                        HttpBody httpBody) {
        this.httpVersion = httpVersion;
        this.httpStatus = httpStatus;
        this.httpHeaders = httpHeaders;
        this.httpBody = httpBody;
    }

    public static String ok(String contentType, String payload) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + payload.getBytes().length + " ",
                "",
                payload);
    }

    public static String redirect(String redirectTo) {
        return String.join("\r\n",
                "HTTP/1.1 301 Redirect ",
                "Location: " + redirectTo);
    }

    public static String found(String redirectTo) {
        return String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: " + redirectTo);
    }

    public static String badRequest() {
        return String.join("\r\n",
                "HTTP/1.1 400 Bad Request ");
    }

    public static String unauthorized() {
        return String.join("\r\n",
                "HTTP/1.1 401 Unauthorized ");
    }

    public static String notFound() {
        return String.join("\r\n",
                "HTTP/1.1 404 Not Found ");
    }

    public static String methodNotAllowed() {
        return String.join("\r\n",
                "HTTP/1.1 405 Method Not Allowed ");
    }

    public static String internalServerError() {
        return String.join("\r\n",
                "HTTP/1.1 500 Internal Server Error ");
    }

    public static HttpResponse empty() {
        return new HttpResponse(HttpStatus.OK, new HttpHeaders(), HttpBody.empty());
    }

    public HttpVersion httpVersion() {
        return httpVersion;
    }

    public HttpStatus httpStatus() {
        return httpStatus;
    }

    public HttpHeaders httpHeaders() {
        return httpHeaders;
    }

    public HttpBody httpBody() {
        return httpBody;
    }

    public void setHttpVersion(HttpVersion httpVersion) {
        this.httpVersion = httpVersion;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public void setHttpHeaders(HttpHeaders httpHeaders) {
        this.httpHeaders = httpHeaders;
    }

    public void setHttpBody(HttpBody httpBody) {
        this.httpBody = httpBody;
    }

    public byte[] getBytes() {
        return new byte[3];
    }

    public void setHttpBody(String body) {
        setHttpBody("*/*", body);
    }

    public void setHttpBody(String contentType, String body) {
        httpHeaders.addHeader("Content-Type", contentType);
        httpHeaders.addHeader("Content-Length", String.valueOf(body.getBytes().length));
        httpBody = HttpBody.of(body);
    }

    public void setLocation(String location) {
        httpHeaders.addHeader("Location", location);
    }

    public String asString() {
        List<String> output = new ArrayList<>();

        output.add(httpVersion.protocol() + " " + httpStatus + " ");
        output.addAll(httpHeaders.asString());
        output.add("");
        output.add(httpBody.body());

        return String.join("\r\n", output);
    }

    public void addHeader(String name, String value) {
        httpHeaders.addHeader(name, value);
    }

    public boolean containsHeader(String headerName) {
        return httpHeaders.hasHeaderName(headerName);
    }
}
