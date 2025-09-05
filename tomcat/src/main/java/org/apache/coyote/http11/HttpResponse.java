package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class HttpResponse {
    
    private final OutputStream outputStream;
    private String contentType = "text/html;charset=utf-8";
    private int status = 200;
    private boolean committed = false;
    
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
    
    public void write(final String content) {
        if (committed) {
            throw new IllegalStateException("Response already committed");
        }
        
        try {
            final String reasonPhrase = getReasonPhrase(status);
            final byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);
            
            final String response = String.join("\r\n",
                    "HTTP/1.1 " + status + " " + reasonPhrase,
                    "Content-Type: " + contentType,
                    "Content-Length: " + contentBytes.length,
                    "",
                    content);
                    
            outputStream.write(response.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
            committed = true;
        } catch (final IOException e) {
            throw new RuntimeException("Failed to write response", e);
        }
    }
    
    private String getReasonPhrase(final int status) {
        return switch (status) {
            case 200 -> "OK";
            case 400 -> "Bad Request";
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
