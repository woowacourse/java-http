package nextstep.jwp.controller;

import java.io.IOException;
import nextstep.jwp.http.ContentType;
import nextstep.jwp.http.FileReader;
import nextstep.jwp.http.HttpError;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;

public class Controller extends AbstractController {

    public static final String INDEX_PAGE = "/index.html";

    public Controller(HttpRequest httpRequest) {
        super(httpRequest);
    }

    @Override
    byte[] get(HttpRequest httpRequest) throws IOException {
        return HttpResponse
                .ok(FileReader.file(httpRequest.uri()), ContentType.findBy(httpRequest.uri()));
    }

    @Override
    byte[] post(HttpRequest httpRequest) throws IOException {
        return HttpResponse
                .ok(FileReader.file(httpRequest.uri()), ContentType.findBy(httpRequest.uri()));
    }

    @Override
    byte[] error(HttpError httpError) throws IOException {
        return HttpResponse.error(HttpError.FORBIDDEN);
    }
}
