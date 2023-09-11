package common.http;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.BiConsumer;

import static common.http.HttpMethod.DELETE;
import static common.http.HttpMethod.GET;
import static common.http.HttpMethod.PATCH;
import static common.http.HttpMethod.POST;
import static common.http.HttpMethod.PUT;

public abstract class AbstractController implements Controller {

    private final Map<HttpMethod, BiConsumer<Request, Response>> methodMapping = new EnumMap<>(HttpMethod.class);

    protected AbstractController() {
        methodMapping.put(GET, this::doGet);
        methodMapping.put(POST, this::doPost);
        methodMapping.put(PUT, this::doPut);
        methodMapping.put(PATCH, this::doPatch);
        methodMapping.put(DELETE, this::doDelete);
    }

    @Override
    public void service(Request request, Response response) {
        methodMapping.get(request.getHttpMethod()).accept(request, response);
    }

    protected void doGet(Request request, Response response) { /* NOOP */ }

    protected void doPost(Request request, Response response) { /* NOOP */ }

    protected void doPut(Request request, Response response) { /* NOOP */ }

    protected void doPatch(Request request, Response response) { /* NOOP */ }

    protected void doDelete(Request request, Response response) { /* NOOP */ }
}
