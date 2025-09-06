package org.apache.coyote.http11.response;

record HttpResponseHeader(ContentType contentType, int contentLength) {
    
    public static HttpResponseHeader of(ContentType contentType, String body) {
        return new HttpResponseHeader(contentType, body.getBytes().length);
    }

    public String toHeaderString() {
        return String.join("\r\n",
                "Content-Type: " + contentType.getValue() + " ",
                "Content-Length: " + contentLength + " ");
    }
}
