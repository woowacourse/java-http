package org.apache.coyote.http11.handler;

import java.io.IOException;
import org.apache.coyote.http11.ContentTypeParser;
import org.apache.coyote.http11.httpmessage.HttpStatus;
import org.apache.coyote.http11.httpmessage.Request;
import org.apache.coyote.http11.httpmessage.Response;

public class IndexHandler extends Handler {

    @Override
    public Response handle(Request request) throws IOException {
        String target = "index.html";

        String resource = findResourceWithPath(target);
        String contentType = ContentTypeParser.parse(target);
        int contentLength = resource.getBytes().length;

        return Response.from(request.getHttpVersion(), HttpStatus.OK,
                contentType, contentLength, resource);
    }
}
