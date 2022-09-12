package org.apache.coyote.http11.response;

import static org.apache.coyote.http11.support.HeaderField.CONTENT_LENGTH;
import static org.apache.coyote.http11.support.HeaderField.CONTENT_TYPE;
import static org.apache.coyote.http11.support.HeaderField.LOCATION;

import java.io.IOException;
import org.apache.coyote.http11.support.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;

public class HttpResponseBuilder {

    private static String TYPE_UTF_8 = ";charset=utf-8 ";

    public static HttpResponse ok(HttpRequest request) throws IOException {
        final String requestUri = request.getRequestUri();
        final ResponseBody body = ResponseBody.from(requestUri);
        final ResponseHeaders headers = ResponseHeaders.create()
                .addHeader(CONTENT_TYPE, ContentType.find(requestUri) + TYPE_UTF_8)
                .addHeader(CONTENT_LENGTH, body.getBodyLength());
        return HttpResponse.create(HttpStatus.OK, headers, body);
    }

    public static HttpResponse found(String redirectUrl) {
        final ResponseBody body = new ResponseBody();
        final ResponseHeaders headers = ResponseHeaders.create()
                .addHeader(LOCATION, redirectUrl);
        return HttpResponse.create(HttpStatus.FOUND, headers, body);
    }

    public static HttpResponse found(String redirectUrl, ResponseHeaders headers) {
        final ResponseBody body = new ResponseBody();
        headers.addHeader(LOCATION, redirectUrl);
        return HttpResponse.create(HttpStatus.FOUND, headers, body);
    }
}
