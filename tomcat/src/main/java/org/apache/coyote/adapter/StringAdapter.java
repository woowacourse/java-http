package org.apache.coyote.adapter;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.handler.DefaultHandler;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.request.Request;
import org.apache.coyote.response.HttpStatus;
import org.apache.coyote.response.Response;
import org.apache.coyote.response.ResponseBody;
import org.apache.coyote.response.ResponseHeader;

public class StringAdapter implements Adapter {

    @Override
    public Response doHandle(Request request) {
        DefaultHandler defaultHandler = new DefaultHandler();
        String handlerResponse = defaultHandler.response();

        ResponseBody responseBody = new ResponseBody(handlerResponse);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", ContentType.HTML.getValue() + ";charset=utf-8");
        headers.put("Content-Length", responseBody.getLength());
        ResponseHeader responseHeader = new ResponseHeader(request.getProtocol(), HttpStatus.OK, headers);
        return new Response(responseHeader, responseBody);
    }
}
