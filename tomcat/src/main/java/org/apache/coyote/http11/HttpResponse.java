package org.apache.coyote.http11;

public class HttpResponse {

    private final HttpExtensionType httpExtensionType;
    private final String responseBody;

    public HttpResponse(final HttpExtensionType httpExtensionType, final String responseBody) {
        this.httpExtensionType = httpExtensionType;
        this.responseBody = responseBody;
    }

    public String extractResponse() {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + httpExtensionType.getContentType() + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
