package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    
    private final OutputStream outputStream;
    private String contentType = "text/html;charset=utf-8";
    private int status = 200;
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
    
    public void setStatus(final int status) {
        if (committed) {
            throw new IllegalStateException("Response already committed");
        }
        this.status = status;
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
            final String reasonPhrase = getReasonPhrase(status);
            final byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);
            
            final StringBuilder responseBuilder = new StringBuilder();
            responseBuilder.append("HTTP/1.1 ").append(status).append(" ").append(reasonPhrase).append("\r\n");
            responseBuilder.append("Content-Type: ").append(contentType).append("\r\n");
            responseBuilder.append("Content-Length: ").append(contentBytes.length).append("\r\n");
            
            // Set-Cookie 헤더 추가
            for (final Map.Entry<String, String> cookie : cookies.entrySet()) {
                responseBuilder.append("Set-Cookie: ").append(cookie.getKey()).append("=").append(cookie.getValue()).append("\r\n");
            }
            
            responseBuilder.append("\r\n");
            responseBuilder.append(content);
                    
            outputStream.write(responseBuilder.toString().getBytes(StandardCharsets.UTF_8));
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
            final StringBuilder responseBuilder = new StringBuilder();
            responseBuilder.append("HTTP/1.1 302 Found\r\n");
            responseBuilder.append("Location: ").append(location).append("\r\n");
            responseBuilder.append("Content-Length: 0\r\n");
            
            // Set-Cookie 헤더 추가
            for (final Map.Entry<String, String> cookie : cookies.entrySet()) {
                responseBuilder.append("Set-Cookie: ").append(cookie.getKey()).append("=").append(cookie.getValue()).append("\r\n");
            }
            
            responseBuilder.append("\r\n");
                    
            outputStream.write(responseBuilder.toString().getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
            committed = true;
        } catch (final IOException e) {
            throw new RuntimeException("Failed to send redirect", e);
        }
    }
    
    private String getReasonPhrase(final int status) {
        return switch (status) {
            case 200 -> "OK";
            case 302 -> "Found";
            case 400 -> "Bad Request";
            case 401 -> "Unauthorized";
            case 404 -> "Not Found";
            case 405 -> "Method Not Allowed";
            case 500 -> "Internal Server Error";
            default -> "Unknown";
        };
    }
    
    public boolean isCommitted() {
        return committed;
    }
}
