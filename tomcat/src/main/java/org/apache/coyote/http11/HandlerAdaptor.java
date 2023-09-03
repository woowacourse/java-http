package org.apache.coyote.http11;

import static org.apache.coyote.http11.common.Method.GET;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import nextstep.jwp.controller.UserController;
import org.apache.coyote.http11.common.HandlerMapping;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;
import org.apache.coyote.http11.util.UriComponentsBuilder;

public class HandlerAdaptor {

    private static final Map<HandlerMapping, Function<Map<String, List<String>>, Response>> handlerMethods = new HashMap<>(
            Map.of(
                    new HandlerMapping(GET, "/login"), UserController::login
            )
    );

    private static Response getMapping(String path, Map<String, List<String>> queryParams) {
        return handlerMethods.get(new HandlerMapping(GET, path))
                .apply(queryParams);
    }

    public Response get(Request request) {
        String uri = request.getUri();
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.of(uri);

        String path = uriComponentsBuilder.getPath();
        Map<String, List<String>> queryParams = uriComponentsBuilder.build().getQueryParams();

        return getMapping(path, queryParams);
    }
}
