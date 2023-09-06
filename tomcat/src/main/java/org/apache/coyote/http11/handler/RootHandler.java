package org.apache.coyote.http11.handler;

import static org.apache.coyote.http11.message.HttpHeaders.CONTENT_LENGTH;
import static org.apache.coyote.http11.message.HttpHeaders.CONTENT_TYPE;

import java.util.Map;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.message.Headers;
import org.apache.coyote.http11.message.HttpStatus;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.message.response.ResponseBody;

public class RootHandler extends Handler {

    @Override
    public HttpResponse handle(HttpRequest httpRequest) {
        String resource = "Hello world!";

        Headers headers = Headers.fromMap(Map.of(
                CONTENT_TYPE, ContentType.parse(resource),
                CONTENT_LENGTH, String.valueOf(resource.getBytes().length)
        ));
        ResponseBody responseBody = new ResponseBody(resource);

        return new HttpResponse(httpRequest.getHttpVersion(), HttpStatus.OK,
                headers, responseBody);
    }
}
