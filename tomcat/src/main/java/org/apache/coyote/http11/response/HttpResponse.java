package org.apache.coyote.http11.response;

import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpVersion;

public class HttpResponse {

    private final HttpVersion httpVersion;
    private final ContentType contentType;
    private final String responseBody;

    public HttpResponse(final HttpVersion httpVersion, final ContentType contentType, final String responseBody) {
        this.httpVersion = httpVersion;
        this.contentType = contentType;
        this.responseBody = responseBody;
    }

    public static HttpResponse of(final HttpRequest httpRequest, final String responseBody) {
        final HttpVersion httpVersion = httpRequest.getHttpVersion();
        final ContentType contentType = httpRequest.getContentType();
        return new HttpResponse(httpVersion, contentType, responseBody);
    }

    public byte[] getResponseAsBytes() {
        return createResponse().getBytes();
    }

    private String createResponse() {
        return String.join("\r\n",
                httpVersion.getValue() + " 200 OK ",
                "Content-Type: " + contentType.getValue(),
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
