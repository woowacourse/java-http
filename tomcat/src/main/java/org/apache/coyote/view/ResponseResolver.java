package org.apache.coyote.view;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.request.Request;
import org.apache.coyote.response.Response;
import org.apache.coyote.response.ResponseBody;
import org.apache.coyote.response.ResponseHeader;

public class ResponseResolver {

    public Response resolve(Request request, Resource viewResource) {
        ResponseBody responseBody = new ResponseBody(viewResource.getValue());

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", request.getResourceTypes() + ";charset=utf-8");
        headers.put("Content-Length", responseBody.getLength());
        ResponseHeader responseHeader = new ResponseHeader(request.getProtocol(), viewResource.getHttpStatus(), headers);
        return new Response(responseHeader, responseBody);
    }
}
