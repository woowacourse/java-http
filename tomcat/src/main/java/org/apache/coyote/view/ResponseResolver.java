package org.apache.coyote.view;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.request.Request;
import org.apache.coyote.response.Response;
import org.apache.coyote.response.ResponseBody;
import org.apache.coyote.response.ResponseHeader;

public class ResponseResolver {

    public Response resolve(Request request, Resource resource) {
        ResponseBody responseBody = new ResponseBody(resource.getValue());

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", request.getResourceTypes() + ";charset=utf-8");
        headers.put("Content-Length", responseBody.getLength());
        if (resource.hasCookie()) {
            headers.put("Set-Cookie", resource.getHttpCookie());
        }
        ResponseHeader responseHeader = new ResponseHeader(request.getProtocol(), resource.getHttpStatus(), headers);
        return new Response(responseHeader, responseBody);
    }
}
