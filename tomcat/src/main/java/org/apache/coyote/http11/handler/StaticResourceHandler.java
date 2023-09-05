package org.apache.coyote.http11.handler;

import static org.apache.coyote.http11.message.HttpHeaders.CONTENT_LENGTH;
import static org.apache.coyote.http11.message.HttpHeaders.CONTENT_TYPE;

import java.io.IOException;
import java.util.Map;
import org.apache.coyote.http11.ContentTypeParser;
import org.apache.coyote.http11.message.Headers;
import org.apache.coyote.http11.message.HttpStatus;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.request.RequestURI;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.message.response.ResponseBody;

public class StaticResourceHandler extends Handler {

    @Override
    public HttpResponse handle(HttpRequest httpRequest) throws IOException {
        RequestURI requestURI = httpRequest.getRequestURI();
        String absolutePath = requestURI.absolutePath();

        String resource = findResourceWithPath(absolutePath);
        Headers headers = Headers.fromMap(Map.of(
                CONTENT_TYPE, ContentTypeParser.parse(absolutePath),
                CONTENT_LENGTH, String.valueOf(resource.getBytes().length)
        ));
        ResponseBody responseBody = new ResponseBody(resource);

        return new HttpResponse(httpRequest.getHttpVersion(), HttpStatus.OK,
                headers, responseBody);
    }
}
