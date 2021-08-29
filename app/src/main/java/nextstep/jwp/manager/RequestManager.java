package nextstep.jwp.manager;

import nextstep.jwp.request.ClientRequest;
import nextstep.jwp.request.RequestParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class RequestManager {
    private final StaticResourceManager staticResourceManager;
    private final DynamicWebManager dynamicWebManager;

    public RequestManager(StaticResourceManager staticResourceManager, DynamicWebManager dynamicWebManager) {
        this.staticResourceManager = staticResourceManager;
        this.dynamicWebManager = dynamicWebManager;
    }

    public void handle(InputStream inputStream, OutputStream outputStream) throws IOException {
        final RequestParser requestParser = new RequestParser(inputStream);
        final ClientRequest clientRequest = requestParser.extractClientRequest();

        if (staticResourceManager.canHandle(clientRequest)) {
            staticResourceManager.handle(clientRequest, outputStream);
            return;
        }

        if (dynamicWebManager.canHandle(clientRequest)) {
            final String result = dynamicWebManager.handle(clientRequest);
            staticResourceManager.handleDynamicResult(result, outputStream);
            return;
        }

        staticResourceManager.handleNotFound(outputStream);
    }
}
