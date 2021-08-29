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
        final ClientRequest clientRequest = new RequestParser(inputStream).extractClientRequest();

        if (staticResourceManager.canHandle(clientRequest)) {
            staticResourceManager.handle(clientRequest, outputStream);
            return;
        }

        if (dynamicWebManager.canHandle(clientRequest)) {
            final String dynamicWebProcessResult = dynamicWebManager.handle(clientRequest);
            staticResourceManager.handleDynamicResult(dynamicWebProcessResult, outputStream);
            return;
        }

        staticResourceManager.handleNotFound(outputStream);
    }
}
