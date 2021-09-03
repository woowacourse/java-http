package nextstep.jwp.controller;

import nextstep.jwp.http.HttpMethod;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.function.Function;

import static nextstep.jwp.controller.JwpController.NOT_FOUND_RESPONSE;

public abstract class AbstractController implements Controller {
    protected static final Logger log = LoggerFactory.getLogger(AbstractController.class);

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        if (request.getHttpMethod().equals(HttpMethod.GET)) {
            doGet(request, response);
            return;
        }
        doPost(request, response);
    }

    protected abstract void doGet(HttpRequest request, HttpResponse response);

    protected abstract void doPost(HttpRequest request, HttpResponse response);

    protected HttpResponse getHttpResponse(HttpRequest request, Map<String, Function<HttpRequest, HttpResponse>> mappedFunction) {
        return mappedFunction.entrySet().stream()
                .filter(entry -> request.containsFunctionInUrl(entry.getKey()))
                .map(entry -> entry.getValue().apply(request))
                .findAny()
                .orElse(NOT_FOUND_RESPONSE);
    }
}
