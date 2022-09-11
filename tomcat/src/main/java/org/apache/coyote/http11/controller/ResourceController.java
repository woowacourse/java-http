package org.apache.coyote.http11.controller;

import static nextstep.jwp.exception.ExceptionType.SERVER_EXCEPTION;

import com.sun.jdi.InternalException;
import java.io.IOException;
import java.net.URISyntaxException;
import nextstep.jwp.util.Parser;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class ResourceController implements Handler {

    private static final ResourceController INSTANCE = new ResourceController();

    private ResourceController() {
    }

    @Override
    public void handle(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        final String path = httpRequest.getUrl();
        final String fileName = Parser.convertResourceFileName(path);

        try {
            httpResponse.setOkResponse(fileName);

        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            throw new InternalException(SERVER_EXCEPTION.getMessage());
        }
    }

    public static ResourceController getINSTANCE() {
        return INSTANCE;
    }
}
