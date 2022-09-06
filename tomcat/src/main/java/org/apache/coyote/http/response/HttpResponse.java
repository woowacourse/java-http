package org.apache.coyote.http.response;

import org.apache.coyote.http.ContentType;
import org.apache.coyote.http.HttpVersion;

public class HttpResponse {

    private final HttpVersion version;
    private final HttpStatus httpStatus;
    private final ContentType contentType;
    private final ResponseBody responseBody;

    private HttpResponse(final HttpVersion version, final HttpStatus httpStatus, final ContentType contentType,
                        final ResponseBody responseBody) {
        this.version = version;
        this.httpStatus = httpStatus;
        this.contentType = contentType;
        this.responseBody = responseBody;
    }

    public static HttpResponse create200Response(final ContentType contentType,
                                                 final ResponseBody responseBody) {
        return new HttpResponse(HttpVersion.HTTP11, HttpStatus.OK, contentType, responseBody);
    }

    public static HttpResponse create302Response(final ResponseBody responseBody) {
        return new HttpResponse(HttpVersion.HTTP11, HttpStatus.FOUND, ContentType.HTML, responseBody);
    }

    public String convertTemplate() {
        if (httpStatus.equals(HttpStatus.FOUND)) {
            return String.join("\r\n",
                    version.getVersion() + " " + httpStatus.getHttpStatusMessage(),
                    "Location: /index.html ");
        }
        return String.join("\r\n",
                version.getVersion() + " " + httpStatus.getHttpStatusMessage(),
                "Content-Type: " + this.contentType.getContentType() + ";charset=utf-8 ",
                "Content-Length: " + responseBody.calculateLength() + " ",
                "",
                this.responseBody.getBody());
    }
}
