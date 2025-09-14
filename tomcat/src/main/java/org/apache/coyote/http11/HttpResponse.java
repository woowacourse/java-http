package org.apache.coyote.http11;

public class HttpResponse {
    private final String version;
    private final HttpStatus status;
    private final HttpHeaders headers;
    private final String body;
    
    public HttpResponse(String version, HttpStatus status, HttpHeaders headers, String body) {
        this.version = version;
        this.status = status;
        this.headers = headers;
        this.body = body;
    }
    
    public HttpStatus getStatus() {
        return status;
    }
    
    public String getBody() {
        return body;
    }
    
    public String toHttpString() {
        StringBuilder response = new StringBuilder();
        response.append(version).append(" ")
                .append(status.getCode())
                .append(" ")
                .append(status.getReasonPhrase())
                .append("\r\n");
        
        response.append(headers.toString());
        
        response.append("\r\n");
        if (body != null && !body.isEmpty()) {
            response.append(body);
        }
        
        return response.toString();
    }
}
