package org.apache.http;

import org.apache.http.info.ContentType;

public class BasicHttpResponse implements HttpResponse {

    private final String response;
    private final String contentType;

    public BasicHttpResponse(final String response, final String contentType) {
        this.response = response;
        this.contentType = contentType;
    }

    public static BasicHttpResponse from(final String response) {
        return new BasicHttpResponse(response, ContentType.TEXT_HTML.getName());
    }

    public static BasicHttpResponse from(final String response, final String contentType) {
        return new BasicHttpResponse(response, contentType);
    }

    @Override
    public String getResponseHttpMessage() {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                String.format("Content-Type: %s ", contentType),
                "Content-Length: " + this.response.getBytes().length + " ",
                "",
                this.response);
    }
}
