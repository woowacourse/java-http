package customservlet;

import customservlet.exception.NotFoundHandlerException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.coyote.http11.http.HttpRequest;

public class MappedRequestHandlers {

    private static final MappedRequestHandlers mappedRequestHandlers = new MappedRequestHandlers();
    private static final Map<RequestHandlerInfo, RequestHandler> urlMap = new LinkedHashMap<>();

    private MappedRequestHandlers() {
    }

    public static MappedRequestHandlers getInstance() {
        return mappedRequestHandlers;
    }

    public void addRequestHandler(final RequestHandlerInfo requestHandlerInfo, final RequestHandler requestHandler) {
        urlMap.put(requestHandlerInfo, requestHandler);
    }

    public RequestHandler getHandler(final HttpRequest request) {
        return urlMap.entrySet()
                .stream()
                .filter(it -> it.getKey().canSupport(request))
                .map(Entry::getValue)
                .findFirst()
                .orElseThrow(NotFoundHandlerException::new);
    }
}
