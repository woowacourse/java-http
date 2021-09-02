package nextstep.jwp.framework.manager;

import nextstep.jwp.framework.http.request.HttpRequest;
import nextstep.jwp.framework.http.request.RequestParser;
import nextstep.jwp.framework.http.response.HttpResponse;
import nextstep.jwp.framework.http.response.ResponseSender;
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
        final HttpRequest httpRequest = RequestParser.of(inputStream).extractHttpRequest();
        final HttpResponse httpResponse = handleClientRequest(httpRequest);
        httpResponse.appendSessionInfo(httpRequest.getSession());
        ResponseSender.of(outputStream).sendResponse(httpResponse);
    }

    private HttpResponse handleClientRequest(HttpRequest httpRequest) {
        try {
            if (staticResourceManager.canHandle(httpRequest)) {
                return staticResourceManager.handle(httpRequest);
            }

            if (dynamicWebManager.canHandle(httpRequest)) {
                final String dynamicWebProcessResult = dynamicWebManager.handle(httpRequest);
                return staticResourceManager.handleDynamicResult(dynamicWebProcessResult);
            }

            return staticResourceManager.handleNotFound();
        } catch (Exception e) {
            log.error("########## internal server error occurred! = {} ##########", e.getMessage());
            return staticResourceManager.handleInternalServerError();
        }
    }
}
