package org.apache.catalina.domain.response;

import com.spring.http.enums.HttpStatus;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.apache.catalina.domain.HttpHeader;
import org.apache.catalina.domain.cookie.HttpCookie;
import org.apache.catalina.domain.request.HttpRequest;

public final class HttpResponse {

    private ResponseStartLine startLine;
    private final HttpHeader header;
    private final List<HttpCookie> setCookies;
    private byte[] body;

    public HttpResponse(ResponseStartLine startLine, HttpHeader headers, byte[] body) {
        this.startLine = startLine;
        this.header = headers;
        this.setCookies = new ArrayList<>();
        this.body = body;
    }

    public HttpResponse(String version) {
        this(new ResponseStartLine(version), new HttpHeader(), null);
    }

    public HttpResponse(HttpRequest request) {
        this(new ResponseStartLine(request), new HttpHeader(), null);
    }

    public ResponseStartLine getStartLine() {
        return startLine;
    }

    public void setStartLine(ResponseStartLine startLine) {
        this.startLine = startLine;
    }

    public void addHeader(String key, String value) {
        this.header.put(key, value);
    }

    public void addSetCookie(HttpCookie cookie) {
        this.setCookies.add(cookie);
    }

    public String getHeaderString() {
        StringBuilder headerString = new StringBuilder(header.toHeaderString());
        
        for (HttpCookie cookie : setCookies) {
            headerString.append("Set-Cookie: ").append(cookie.toString()).append("\r\n");
        }
        
        return headerString.toString();
    }

    public HttpStatus getStatus() {
        return this.startLine.getHttpStatus();
    }

    public void setStatus(HttpStatus httpStatus) {
        startLine.setHttpStatus(httpStatus);
    }

    public void setVersion(String version) {
        startLine.setVersion(version);
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public void sendError(HttpStatus httpStatus, String message) {
        setStatus(httpStatus);
        this.body = message.getBytes(StandardCharsets.UTF_8);
    }
}
