package nextstep.jwp.controller;

import org.apache.coyote.Controller;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import java.util.*;

import static org.apache.coyote.http11.HttpHeaderType.CONTENT_TYPE;
import static org.apache.coyote.http11.response.HttpStatusCode.OK;

public class RootController extends Controller {

    @Override
    public boolean canHandle(final HttpRequest httpRequest) {
        final Map<String, Set<String>> requestType = new HashMap<>(Map.of(
                "/", new HashSet<>(Set.of("GET"))
        ));

        if (requestType.containsKey(httpRequest.getTarget())) {
            final Set<String> methodType = requestType.get(httpRequest.getTarget());
            return methodType.contains(httpRequest.getMethod());
        }
        return false;
    }

    @Override
    public void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        httpResponse.addHeader(CONTENT_TYPE, "text/html;charset=utf-8");
        httpResponse.setStatusCode(OK);
        httpResponse.setBody("Hello world!");
    }

    @Override
    public void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) {
    }
}
