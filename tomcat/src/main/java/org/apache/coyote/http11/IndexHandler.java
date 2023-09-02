package org.apache.coyote.http11;

import java.io.IOException;

public class IndexHandler extends Handler {

    @Override
    public Response handle(Request request) throws IOException {
        String target = "index.html";

        String resource = findResourceWithPath(target);
        String contentType = ContentTypeParser.parse(target);
        int contentLength = resource.getBytes().length;

        return Response.from(request.getHttpVersion(), "200 OK",
                contentType, contentLength, resource);
    }
}
