package org.apache.catalina.servlets.http.response;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.catalina.servlets.http.Cookie;

public class HttpResponse {

    private static final String CRLF = "\r\n";
    private static final String START_LiNE_FORMAT = "%s %s %s ";
    private static final String HEADER_FORMAT_TEMPLATE = "%s: %s " + CRLF;

    private final OutputStream outputStream;

    private HttpStatus httpStatus;
    private String protocolVersion;
    private final Map<String, String> headers;
    private String responseBody;

    private PrintWriter writer;

    public HttpResponse(OutputStream outputStream) {
        this.httpStatus = HttpStatus.OK;
        this.protocolVersion = "HTTP/1.1";
        this.headers = new LinkedHashMap<>();
        this.responseBody = "";
        this.outputStream = outputStream;
    }

    public void sendRedirect(String location) {
        headers.put("Location", location);
        setStatus(HttpStatus.FOUND.getStatusCode());
    }

    public void setStatus(int status) {
        this.httpStatus = HttpStatus.from(status);
    }

    public void setContentLength(int contentLength) {
        headers.put("Content-Length", String.valueOf(contentLength));
    }

    public void setContentType(String contentType) {
        headers.put("Content-Type", contentType);
    }

    public void addCookie(Cookie cookie) {
        headers.put("Set-Cookie", cookie.toString());
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void write() {
        PrintWriter responseWriter = getWriter();
        responseWriter.write(START_LiNE_FORMAT.formatted(
                protocolVersion, httpStatus.getStatusCode(), httpStatus.name()
        ));
        responseWriter.write(CRLF);
        headers.forEach((name, value) -> responseWriter.write(HEADER_FORMAT_TEMPLATE.formatted(name, value)));
        responseWriter.write(CRLF);
        responseWriter.write(responseBody);
    }

    public PrintWriter getWriter() {
        if (writer == null) {
            writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(outputStream)));
        }
        return writer;
    }
}
