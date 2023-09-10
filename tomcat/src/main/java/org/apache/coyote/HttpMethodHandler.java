package org.apache.coyote;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.BiConsumer;
import org.apache.coyote.common.HttpMethod;
import org.apache.coyote.common.HttpRequest;
import org.apache.coyote.common.HttpResponse;
import org.apache.coyote.exception.MethodNotAllowedException;

public abstract class HttpMethodHandler extends AbstractHandler {

    protected final Map<HttpMethod, BiConsumer<HttpRequest, HttpResponse>> actions = new EnumMap<>(HttpMethod.class);

    @Override
    public void handle(HttpRequest request, HttpResponse response) {
        HttpMethod httpMethod = request.getHttpMethod();
        actions.getOrDefault(httpMethod, (ignore1, ignore2) -> {
            throw new MethodNotAllowedException(actions.keySet());
        }).accept(request, response);
    }
}
