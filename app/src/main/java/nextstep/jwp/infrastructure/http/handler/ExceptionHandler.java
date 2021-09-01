package nextstep.jwp.infrastructure.http.handler;

import nextstep.jwp.infrastructure.http.FileResolver;
import nextstep.jwp.infrastructure.http.request.HttpRequest;
import nextstep.jwp.infrastructure.http.response.HttpResponse;
import nextstep.jwp.infrastructure.http.response.StatusCode;

public class ExceptionHandler implements Handler{

    private final FileHandler fileHandler;

    public ExceptionHandler(final FileHandler fileHandler) {
        this.fileHandler = fileHandler;
    }

    @Override
    public void handle(final HttpRequest request, final HttpResponse response) throws Exception {
        fileHandler.respondWithFile(response, "/500.html", StatusCode.INTERNAL_SERVER_ERROR);
    }
}
