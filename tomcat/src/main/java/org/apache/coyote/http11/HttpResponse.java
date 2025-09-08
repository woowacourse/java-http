package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    
    private final OutputStream outputStream;
    private String contentType = "text/html;charset=utf-8";
    private HttpStatus status = HttpStatus.OK;
    private boolean committed = false;
    private final Map<String, String> cookies = new HashMap<>();
    
    public HttpResponse(final OutputStream outputStream) {
        this.outputStream = outputStream;
    }
    
    public void setContentType(final String contentType) {
        if (committed) {
            throw new IllegalStateException("Response already committed");
        }
        this.contentType = contentType;
    }
    
    public void setStatus(final HttpStatus status) {
        if (committed) {
            throw new IllegalStateException("Response already committed");
        }
        this.status = status;
    }
    
    public void setStatus(final int statusCode) {
        setStatus(HttpStatus.fromCode(statusCode));
    }
    
    public void addCookie(final String name, final String value) {
        if (committed) {
            throw new IllegalStateException("Response already committed");
        }
        cookies.put(name, value);
    }
    
    public void write(final String content) {
        if (committed) {
            throw new IllegalStateException("Response already committed");
        }
        
        try {
            final byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);
            
            final StringBuilder cookieHeaders = new StringBuilder();
            for (final Map.Entry<String, String> cookie : cookies.entrySet()) {
                cookieHeaders.append("Set-Cookie: ").append(cookie.getKey()).append("=").append(cookie.getValue()).append("\r\n");
            }
            
            final String response = """
                HTTP/1.1 %d %s\r
                Content-Type: %s\r
                Content-Length: %d\r
                %s\r
                %s""".formatted(status.getCode(), status.getReasonPhrase(), contentType, contentBytes.length, cookieHeaders.toString(), content);
                    
            outputStream.write(response.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
            committed = true;
        } catch (final IOException e) {
            throw new RuntimeException("Failed to write response", e);
        }
    }
    
    public void sendRedirect(final String location) {
        if (committed) {
            throw new IllegalStateException("Response already committed");
        }
        
        try {
            final StringBuilder cookieHeaders = new StringBuilder();
            for (final Map.Entry<String, String> cookie : cookies.entrySet()) {
                cookieHeaders.append("Set-Cookie: ").append(cookie.getKey()).append("=").append(cookie.getValue()).append("\r\n");
            }
            
            final String response = """
                HTTP/1.1 302 Found\r
                Location: %s\r
                Content-Length: 0\r
                %s\r
                """.formatted(location, cookieHeaders.toString());
                    
            outputStream.write(response.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
            committed = true;
        } catch (final IOException e) {
            throw new RuntimeException("Failed to send redirect", e);
        }
    }
    
    public boolean isCommitted() {
        return committed;
    }
}
