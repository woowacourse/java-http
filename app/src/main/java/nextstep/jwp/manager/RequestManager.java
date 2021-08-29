package nextstep.jwp.manager;

import nextstep.jwp.request.ClientRequest;
import nextstep.jwp.request.RequestParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class RequestManager {

    private static final Logger log = LoggerFactory.getLogger(RequestManager.class);

    private final StaticResourceManager staticResourceManager;
    private final DynamicWebManager dynamicWebManager;

    public RequestManager(StaticResourceManager staticResourceManager, DynamicWebManager dynamicWebManager) {
        this.staticResourceManager = staticResourceManager;
        this.dynamicWebManager = dynamicWebManager;
    }

    public void handle(InputStream inputStream, OutputStream outputStream) throws IOException {
        final ClientRequest clientRequest = new RequestParser(inputStream).extractClientRequest();

        try {
            handleClientRequest(outputStream, clientRequest);
        } catch (Exception e) {
            log.error("@@@@@@@internal server error occurred! = {}@@@@@@@", e.getMessage());
            staticResourceManager.handleInternalServerError(outputStream);
        }
    }

    private void handleClientRequest(OutputStream outputStream, ClientRequest clientRequest) throws IOException {
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
