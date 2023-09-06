package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.Response;
import org.apache.coyote.http11.util.Resource;

public class StaticHandler extends Handler{
    private Handler next;

    @Override
    public void setNext(Handler handler) {
        this.next = handler;
    }

    @Override
    public String getResponse(Request request) {
        String uri = request.getUri();
        if (!uri.contains("?") && uri.contains(".")) {
            String contentType = uri.split("\\.")[1];
            return Response.builder()
                    .status(HttpStatus.OK)
                    .contentType(contentType)
                    .responseBody(Resource.getFile(uri))
                    .build().getResponse();
        }
        return next.getResponse(request);
    }
}
