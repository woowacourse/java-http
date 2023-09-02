package org.apache.coyote.http11.handler;

import java.util.Map;
import org.apache.coyote.http11.ContentTypeParser;
import org.apache.coyote.http11.httpmessage.*;

public class DefaultHandler extends Handler {

    @Override
    public Response handle(Request request) {
        String resource = "Hello world!";

        Headers headers = new Headers(Map.of(
                "Content-Type", ContentTypeParser.parse(resource),
                "Content-Length", String.valueOf(resource.getBytes().length)
        ));
        ResponseBody responseBody = new ResponseBody(resource);

        return Response.from(request.getHttpVersion(), HttpStatus.OK,
                headers, responseBody);
    }
}
