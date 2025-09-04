package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;

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
            final String response = String.join("\r\n",
                    "HTTP/1.1 " + status + " OK",
                    "Content-Type: " + contentType,
                    "Content-Length: " + content.getBytes().length,
                    "",
                    content);
                    
            outputStream.write(response.getBytes());
            outputStream.flush();
            committed = true;
        } catch (final IOException e) {
            throw new RuntimeException("Failed to write response", e);
        }
    }
    
    public boolean isCommitted() {
        return committed;
    }
}
