package org.apache.coyote.http11.response;

record HttpResponseHeader(MimeType mimeType, int contentLength) {
    
    public static HttpResponseHeader of(MimeType mimeType, String body) {
        return new HttpResponseHeader(mimeType, body.getBytes().length);
    }

    public String toHeaderString() {
        return String.join("\r\n",
                "Content-Type: " + mimeType.getValue() + " ",
                "Content-Length: " + contentLength + " ");
    }
}
