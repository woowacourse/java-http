package org.apache.coyote.http11.handler;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.controller.RequestFunction;
import org.apache.coyote.http11.controller.ViewFunction;
import org.apache.coyote.http11.exception.NoSuchApiException;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.Response;
import org.apache.coyote.http11.util.Resource;

public class HandlerAdapter {
    private final Map<RequestMapper, RequestFunction> requestFunctions;
    private final Map<RequestMapper, ViewFunction> viewFunctions;

    public HandlerAdapter() {
        this.requestFunctions = new HashMap<>();
        this.viewFunctions = new HashMap<>();
    }

    public Response mapping(Request request) {
        RequestMapper requestInfo
                = new RequestMapper(request.getMethod(), request.getPath());
        if (requestFunctions.containsKey(requestInfo)) {
            return requestFunctions.get(requestInfo).getResponse(request);
        }
        if (viewFunctions.containsKey(requestInfo)) {
            return viewFunctions.get(requestInfo).getResponse();
        }
        if (request.getPath().contains(".")) {
            String contentType = request.getPath().split("\\.")[1];
            return Response.builder()
                    .status(HttpStatus.OK)
                    .contentType(contentType)
                    .responseBody(Resource.getFile(request.getPath()))
                    .build();
        }
        throw new NoSuchApiException();
    }

    public void addRequestFunctions(HttpMethod httpMethod, String path, RequestFunction function) {
        requestFunctions.put(new RequestMapper(httpMethod, path), function);
    }

    public void addNonRequestController(HttpMethod httpMethod, String path, ViewFunction function) {
        viewFunctions.put(new RequestMapper(httpMethod, path), function);
    }
}
