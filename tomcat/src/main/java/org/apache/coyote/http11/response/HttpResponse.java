package org.apache.coyote.http11.response;

public class HttpResponse {

    private StatusCode statusCode;
    private final HttpResponseHeader header;
    private byte[] body;

    public HttpResponse(StatusCode statusCode) {
        this.statusCode = statusCode;
        this.header = new HttpResponseHeader();
    }

    public HttpResponse() {
        this.header = new HttpResponseHeader();
    }

    public void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public void addHeader(String key, String value) {
        header.put(key, value);
    }

    public void setBody(String body) {
        this.body = body.getBytes();
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public byte[] buildResponse() {
        StringBuilder response = new StringBuilder();
        response.append("HTTP/1.1 ")
                .append(statusCode.getCode())
                .append(" ")
                .append(statusCode.getMessage())
                .append(" \r\n");
        response.append(header.buildResponse());
        response.append("\r\n");
        if (body != null) {
            response.append(new String(body));
        }

        return response.toString().getBytes();
    }
}
