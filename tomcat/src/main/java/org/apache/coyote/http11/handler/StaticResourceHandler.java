package org.apache.coyote.http11.handler;

import java.io.IOException;
import org.apache.coyote.http11.ContentTypeParser;
import org.apache.coyote.http11.httpmessage.Request;
import org.apache.coyote.http11.httpmessage.RequestURI;
import org.apache.coyote.http11.httpmessage.Response;

public class StaticResourceHandler extends Handler {

    @Override
    public Response handle(Request request) throws IOException {
        RequestURI requestURI = request.getRequestURI();
        String target = requestURI.absolutePath();

        String resource = findResourceWithPath(target);
        String contentType = ContentTypeParser.parse(target);
        int contentLength = resource.getBytes().length;

        return Response.from(request.getHttpVersion(), "200 OK",
                contentType, contentLength, resource);
    }
}
