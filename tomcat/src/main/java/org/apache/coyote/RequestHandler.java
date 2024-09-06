package org.apache.coyote;

import java.io.IOException;

import org.apache.coyote.handler.LoginHandler;
import org.apache.coyote.handler.RegisterHandler;
import org.apache.coyote.handler.ResourceHandler;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponseGenerator;

public class RequestHandler {
    private static final String PATH_DELIMITER = "/";

    // TODO: mapping handler method
    public String handle(final HttpRequest httpRequest) throws IOException {
        final String path = httpRequest.getPath();
        final String[] paths = path.split(PATH_DELIMITER);

        if (paths.length == 0) {
            return handleRoot();
        }

        final String resourceName = paths[paths.length - 1];
        if (resourceName.contains(".")) {
            return ResourceHandler.getInstance().handleSimpleResource(resourceName);
        }

        return handleURL(httpRequest);
    }

    private String handleRoot() {
        final String responseBody = "Hello world!";
        return HttpResponseGenerator.getOkResponse("text/html", responseBody);
    }

    // TOOD: change naming
    private String handleURL(final HttpRequest httpRequest) throws IOException {
        final String uri = httpRequest.getUrl();
        if (uri.contains("login")) {
            return LoginHandler.getInstance().processLoginRequest(httpRequest);
        }

        if (uri.contains("register")) {
            return RegisterHandler.getInstance().processRegisterRequest(httpRequest);
        }

        throw new IllegalCallerException("유효하지 않은 기능입니다.");
    }
}
