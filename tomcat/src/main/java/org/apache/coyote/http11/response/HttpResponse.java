package org.apache.coyote.http11.response;

import static org.apache.coyote.http11.response.header.HttpStatusCode.FOUND;
import static org.apache.coyote.http11.response.header.HttpStatusCode.NOT_FOUND;
import static org.apache.coyote.http11.response.header.HttpStatusCode.OK;

import org.apache.coyote.http11.response.header.ContentType;
import org.apache.coyote.http11.response.header.HttpStatusCode;
import org.apache.coyote.http11.response.header.Location;

public class HttpResponse {

    private static final String HTTP_VERSION = "HTTP/1.1 ";

    private final HttpStatusCode httpStatusCode;
    private final Location location;
    private final ContentType contentType;
    private final ResponseBody responseBody;

    public HttpResponse(HttpStatusCode httpStatusCode, Location location,
                        ResponseBody responseBody, ContentType contentType) {
        this.httpStatusCode = httpStatusCode;
        this.location = location;
        this.responseBody = responseBody;
        this.contentType = contentType;
    }

    public static HttpResponse ok(String responseBody, ContentType contentType) {
        return new HttpResponse(OK, Location.None(), new ResponseBody(responseBody), contentType);
    }

    public static HttpResponse notFound(String responseBody) {
        return new HttpResponse(NOT_FOUND, Location.None(), new ResponseBody(responseBody), ContentType.TEXT_HTML);
    }

    public static HttpResponse found(String redirectUri) {
        return new HttpResponse(FOUND, new Location(redirectUri), ResponseBody.NONE(), ContentType.NONE);
    }

    public String getResponse() {
        if (httpStatusCode.isFound()) {
            return String.join("\r\n",
                    HTTP_VERSION + httpStatusCode.getValue(),
                    location.getValue());
        }
        return String.join("\r\n",
                HTTP_VERSION + httpStatusCode.getValue(),
                contentType.getValue(),
                "Content-Length: " + responseBody.getValue().getBytes().length + " ",
                "",
                responseBody.getValue());
    }
}
