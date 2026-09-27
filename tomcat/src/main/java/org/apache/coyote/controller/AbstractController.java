package org.apache.coyote.controller;

import java.util.Map;
import org.apache.coyote.http11.DispatchResult;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public abstract class AbstractController implements Controller {

    private final Map<HttpMethod, ControllerHandler> methodHandlerMap = Map.of(
        HttpMethod.GET, this::doGet,
        HttpMethod.POST, this::doPost
    );

    @Override
    public DispatchResult service(HttpRequest request, HttpResponse response) throws Exception {
        final HttpMethod method = request.method();
        return methodHandlerMap.get(method)
            .handle(request, response);
    }

    protected abstract DispatchResult doPost(HttpRequest request, HttpResponse response)
        throws Exception;

    protected abstract DispatchResult doGet(HttpRequest request, HttpResponse response)
        throws Exception;
}
