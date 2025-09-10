package org.apache.coyote.http11.response;

record HttpResponseHeader(MimeType mimeType, int contentLength, String location) {
    
    public static HttpResponseHeader of(MimeType mimeType, String body) {
        return new HttpResponseHeader(mimeType, body.getBytes().length, null);
    }

    public static HttpResponseHeader redirect(String location) {
        return new HttpResponseHeader(null, 0, location);
    }

    public String toHeaderString() {
        StringBuilder headers = new StringBuilder();
        
        if (mimeType != null) {
            headers.append("Content-Type: ").append(mimeType.getValue()).append(" \r\n");
        }
        
        if (contentLength > 0) {
            headers.append("Content-Length: ").append(contentLength).append(" \r\n");
        }
        
        if (location != null) {
            headers.append("Location: ").append(location).append(" \r\n");
        }
        
        return headers.toString().trim();
    }
}
