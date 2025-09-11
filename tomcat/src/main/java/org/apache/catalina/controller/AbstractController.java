package org.apache.catalina.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11Response;
import org.apache.coyote.http11.HttpMethod;

public abstract class AbstractController implements Controller {

    @Override
    public void service(
            final Http11Request request,
            final Http11Response response
    ) throws Exception {
        if (request.getMethod() == HttpMethod.GET) {
            doGet(request, response);
            return;
        }
        if (request.getMethod() == HttpMethod.POST) {
            doPost(request, response);
        }
    }

    protected void doPost(
            final Http11Request request,
            final Http11Response response
    ) throws Exception {
        response.setStatus(405);
    }

    protected void doGet(
            final Http11Request request,
            final Http11Response response
    ) throws Exception {
        response.setStatus(405);
    }

    protected Map<String, String> extractFirstParamValues(final Map<String, List<String>> params) {
        final Map<String, String> result = new HashMap<>();
        for (var entry : params.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                result.put(entry.getKey(), entry.getValue().getFirst());
            }
        }
        return result;
    }
}
